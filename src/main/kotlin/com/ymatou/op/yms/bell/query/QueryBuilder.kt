package com.ymatou.op.yms.bell.query

import org.influxdb.InfluxDB
import org.influxdb.dto.Query
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Created by xuemingli on 16/8/30.
 */

data class FunctionNode(val expression: String)

object Function {
    private fun function(fn: String, field: String): FunctionNode {
        return FunctionNode("$fn(\"$field\")")
    }

    private fun function2(fn: String, field: String, arg: String): FunctionNode {
        return FunctionNode("$fn(\"$field\", $arg)")
    }

    private fun function(fn: String, inner: FunctionNode): FunctionNode {
        return FunctionNode("$fn(${inner.expression})")
    }
    private fun function2(fn: String, inner: FunctionNode, arg: String): FunctionNode {
        return FunctionNode("$fn(${inner.expression}, $arg)")
    }

    fun count(field: String) = function("count", field)

    fun distinct(field: String) = function("distinct", field)

    fun integral(field: String) = function("integral", field)

    fun mean(field: String) = function("mean", field)

    fun median(field: String) = function("median", field)

    fun spread(field: String) = function("spread", field)

    fun sum(field: String) = function("sum", field)

    fun first(field: String) = function("first", field)

    fun last(field: String) = function("last", field)

    fun max(field: String) = function("max", field)

    fun min(field: String) = function("min", field)

    fun top(field: String, n: Int) = function2("top", field, n.toString())

    fun bottom(field: String, n: Int) = function2("bottom", field, n.toString())

    fun derivative(field: String, unit: String) = function2("derivative", field, unit)

    fun derivative(inner: FunctionNode, unit: String) = function2("derivative", inner, unit)

    fun movingAverage(field: String, window: Int) = function2("moving_average", field, window.toString())

    fun movingAverage(inner: FunctionNode, window: Int) = function2("moving_average", inner, window.toString())
}

data class Time(val value: Long, val unit: TimeUnit) {
    fun toSQL(): String {
        val u = when(unit) {
            TimeUnit.DAYS -> "d"
            TimeUnit.NANOSECONDS -> "ns"
            TimeUnit.MICROSECONDS -> "u"
            TimeUnit.MILLISECONDS -> "ms"
            TimeUnit.SECONDS -> "s"
            TimeUnit.MINUTES -> "m"
            TimeUnit.HOURS -> "h"
        }

        return "$value$u"
    }
}

class QueryNode(
        private val db: InfluxDB,
        private val database: String? = null,
        private val _interval: Time? = null,
        private val _period: Time? = null,
        private val _offset: Time? = null,
        private val _fill: Number? = null,
        private val fieldList: List<String> = listOf(),
        private val measurement: String? = null,
        private val whereNode: WhereNode? = null,
        private val groupBys: List<String> = listOf()) {

    constructor(query: QueryNode, whereNode: WhereNode): this(
            query.db,
            query.database,
            query._interval,
            query._period,
            query._offset,
            query._fill,
            query.fieldList,
            query.measurement,
            whereNode,
            query.groupBys)

    val logger = LoggerFactory.getLogger(javaClass)!!
    val re = Regex("(\\d+)([smhd])")
    val units = mapOf("s" to TimeUnit.SECONDS, "m" to TimeUnit.MINUTES, "h" to TimeUnit.HOURS, "d" to TimeUnit.DAYS)

    private fun timeParse(input: String): Time {
        val m = re.find(input)
        if (m != null) {
            val values = m.groupValues
            return Time(values[1].toLong(), units[values[2]]!!)
        }
        throw RuntimeException("invalidate time string")
    }

    fun where(field: String, op: Operator, value: String): WhereNode {
        val node = WhereNode(this, field, op, value)
        return node
    }

    private fun select(measurement: String, regex: Boolean = false): QueryNode {
        if (!regex) {
            return QueryNode(db, database, _interval, _period, _offset, _fill, fieldList, "\"$measurement\"", whereNode, groupBys)
        }
        return QueryNode(db, database, _interval, _period, _offset, _fill, fieldList, "/$measurement/", whereNode, groupBys)
    }

    fun select(measurement: String): QueryNode {
        return select(measurement, false)
    }

    fun select(pattern: Pattern): QueryNode {
        return select(pattern.pattern(), true)
    }

    fun field(field: String): QueryNode {
        val fs = fieldList.toMutableList()
        fs.add("\"$field\"")
        return QueryNode(db, database, _interval, _period, _offset, _fill, fs, measurement, whereNode, groupBys)
    }

    fun field(fn: FunctionNode): QueryNode {
        val fs = fieldList.toMutableList()
        fs.add(fn.expression)
        return QueryNode(db, database, _interval, _period, _offset, _fill, fs, measurement, whereNode, groupBys)
    }

    fun groupBy(field: String): QueryNode {
        val gs = groupBys.toMutableList()
        gs.add(field)
        return QueryNode(db, database, _interval, _period, _offset, _fill, fieldList, measurement, whereNode, gs)
    }

    fun interval(value: Long): QueryNode {
        return interval(value, TimeUnit.MINUTES)
    }

    fun interval(value: Long, unit: TimeUnit = TimeUnit.MINUTES): QueryNode {
        return QueryNode(db, database, Time(value, unit), _period, _offset, _fill, fieldList, measurement, whereNode, groupBys)
    }

    fun interval(value: String): QueryNode {
        return QueryNode(db, database,  timeParse(value), _period, _offset, _fill, fieldList, measurement, whereNode, groupBys)
    }

    fun period(value: Long): QueryNode {
        return period(value, TimeUnit.MINUTES)
    }

    fun period(value: Long, unit: TimeUnit = TimeUnit.MINUTES): QueryNode {
        return QueryNode(db, database,  _interval, Time(value, unit), _offset, _fill, fieldList, measurement, whereNode, groupBys)
    }

    fun period(value: String): QueryNode {
        return QueryNode(db, database, _interval, timeParse(value), _offset, _fill, fieldList, measurement, whereNode, groupBys)
    }

    fun offset(value: Long): QueryNode {
        return offset(value, TimeUnit.DAYS)
    }

    fun offset(value: Long, unit: TimeUnit = TimeUnit.DAYS): QueryNode {
        return QueryNode(db, database, _interval, _period, Time(value, unit), _fill, fieldList, measurement, whereNode, groupBys)
    }

    fun offset(value: String): QueryNode {
        return QueryNode(db, database, _interval, _period, timeParse(value), _fill, fieldList, measurement, whereNode, groupBys)
    }

    fun fill(value: Number): QueryNode {
        return QueryNode(db, database, _interval, _period, _offset, value, fieldList, measurement, whereNode, groupBys)
    }

    private fun timeFilter(): String {
        val period = _period ?: Time(1, TimeUnit.MINUTES)
        val offset = _offset ?: Time(0, TimeUnit.MILLISECONDS)
        val start = System.currentTimeMillis() - offset.unit.toMillis(offset.value)
        val end = start - period.unit.toMillis(period.value)
        return "time > ${end}ms AND time < ${start}ms"
    }

    fun toSQL(): String {
        val fields = if (fieldList.isEmpty()) "*" else fieldList.joinToString(", ")
        var sql = "SELECT $fields FROM $measurement WHERE ${timeFilter()} "
        if (whereNode != null) {
            sql = "$sql AND ${whereNode.toSQL()}"
        }
        var grouped = false
        if (_interval != null) {
            sql = "$sql GROUP BY time(${_interval.toSQL()})"
            grouped = true
        }

        if (groupBys.isNotEmpty()) {
            val groups = groupBys.joinToString(", ")
            if (grouped) {
                sql = "$sql, $groups"
            } else {
                sql = "$sql GROUP BY $groups"
            }
        }
        if (_fill != null) {
            sql = "$sql fill($_fill)"
        }
        return "$sql ORDER BY time DESC"
    }

    fun query(database: String): ResultSet {
        logger.info(this.toSQL())
        val q = Query(this.toSQL(), database)
        val rs = db.query(q, TimeUnit.MILLISECONDS)
        val offset = _offset ?: Time(0, TimeUnit.MINUTES)
        return ResultSet.create(rs, offset)
    }

    fun query(): ResultSet {
        return query(database!!)
    }
}


enum class Operator(val op: String) {
    EQ("="),
    NE("!="),
    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    MATCH("=~"),
    NOT_MATCH("!~")
}

class Expression(val field: String, val op: Operator, val value: String) {
    fun toSQL(): String {
        if (op == Operator.MATCH || op == Operator.NOT_MATCH) {
            return "\"$field\" ${op.op} /$value/"
        }
        return "\"$field\" ${op.op} '$value'"
    }
}


class WhereNode(val query: QueryNode, val expression: String) {
    constructor(query: QueryNode, field: String, op: Operator, value: String): this(query, Expression(field, op, value).toSQL())
    private var left: WhereNode? = null
    private var right: WhereNode? = null

    fun and(node: WhereNode): WhereNode {
        val root = WhereNode(query, "AND")
        root.left = this
        root.right = node
        return root
    }

    fun and(field: String, op: Operator, value: String): WhereNode {
        return and(WhereNode(query, field, op, value))
    }

    fun or(node: WhereNode): WhereNode {
        val root = WhereNode(query, "OR")
        root.left = this
        root.right = node
        return root
    }

    fun or(field: String, op: Operator, value: String): WhereNode {
        return or(WhereNode(query, field, op, value))
    }

    fun end(): QueryNode {
        return QueryNode(query, this)
    }

    fun toSQL(): String {
        if (left == null && right == null) {
            return expression
        }
        return "${left!!.toSQL()} $expression ${right!!.toSQL()}"
    }
}