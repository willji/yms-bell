package com.ymatou.op.yms.bell.query

import groovy.lang.Closure
import groovy.util.Expando
import org.influxdb.dto.QueryResult
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Created by xuemingli on 16/9/6.
 */

data class Key(val name: String, val tags: Map<String, String>, val timestamp: Long) {
    override fun toString(): String {
        val dt = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        if (tags.size > 0) {
            val series = tags.entries.sortedBy { it.key }.map { "${it.key}=${it.value}" }.joinToString(",")
            return "$series,$dt"
        }
        return dt
    }
}

class Point(val entries: Map<String, Any>): Expando() {
    override fun getProperty(property: String): Any {
        return entries[property] ?: throw RuntimeException("missing field $property")
    }

    fun loop(ctx: Closure<Map.Entry<String, Any>>) {
        entries.forEach { ctx.call(it) }
    }

    fun join(point: Point): Point {
        val newEntries = mutableMapOf<String, Any>()
        newEntries.putAll(entries)
        point.entries.forEach {
            newEntries[it.key] = it.value
        }
        return Point(newEntries)
    }

    fun mapping(map: Map<String, String>): Point {
        val newEntries = mutableMapOf<String, Any>()
        newEntries.putAll(entries)
        map.entries.forEach {
            val value = newEntries.remove(it.key)
            if (value != null) {
                newEntries[it.value] = value
            }
        }
        return Point(newEntries)
    }
}

class ResultSet(val data: Map<Key, Point>) {
    companion object {
        fun create(rs: QueryResult?, offset: Time): ResultSet {
            val data = mutableMapOf<Key, Point>()
            if (rs != null && rs.results.size > 0) {
                rs.results[0].series.forEach {
                    val name = it.name
                    val tags = it.tags ?: mapOf()
                    val columns = it.columns
                    columns.removeAt(0)
                    it.values.forEach {
                        val timestamp = (it.removeAt(0) as Double).toLong() + offset.unit.toMillis(offset.value)
                        val entries = columns.zip(it)
                        val point = Point(entries.toMap())
                        data[Key(name, tags, timestamp)] = point
                    }
                }
            }
            return ResultSet(data)
        }
    }

    fun join(rs: ResultSet): ResultSet {
        val newData = mutableMapOf<Key, Point>()
        newData.putAll(data)
        rs.data.entries.forEach {
            if (newData.containsKey(it.key)) {
                newData[it.key] = newData[it.key]!!.join(it.value)
            } else {
                newData[it.key] = it.value
            }
        }
        return ResultSet(newData)
    }

    fun mapping(map: Map<String, String>): ResultSet {
        val newData = mutableMapOf<Key, Point>()
        data.entries.forEach {
            newData[it.key] = it.value.mapping(map)
        }
        return ResultSet(newData)
    }

    fun lambda(ctx: Closure<ResultSet>): ResultSet {
        ctx.call(this.data)
        return this
    }
}