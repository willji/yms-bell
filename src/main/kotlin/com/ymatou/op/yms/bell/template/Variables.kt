package com.ymatou.op.yms.bell.template

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.KotlinModule
import groovy.lang.GroovyObject
import groovy.lang.GroovyObjectSupport
import groovy.lang.MetaClass
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

/**
 * Created by xuemingli on 2016/10/13.
 */
class Variables private constructor(private val variables: MutableMap<String, Variable>): Cloneable, GroovyObjectSupport() {


    constructor(): this(mutableMapOf())

    private val mapper: ObjectMapper = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule()).build()


    fun setValue(key: String, value: Any?) {
        variables[key]?.set(value)
    }

    fun getAt(p: String): Any? {
        if (!variables.contains(p)) {
            throw RuntimeException("no variable $p")
        }
        val v = variables[p]!!
        return v.value ?: v.defaultValue
    }

    override fun getProperty(p: String): Any? {
        return getAt(p)
    }

    fun getValue(p: String): Any? {
        val v = variables[p] ?: return null
        return v.value ?: v.defaultValue
    }

    fun save(): String {
        val data = variables.map { it.key to it.value.value}
        return mapper.writeValueAsString(data)
    }

    fun fill(src: String): Variables {
        val json: Map<String, Any?> = mapper.readValue(src)
        json.entries.forEach {
            setValue(it.key, it.value)
        }
        return this
    }

    fun get(): Map<String, Variable> {
        return variables
    }

    fun define(map: Map<String, Any>) {
        val v = Variable.create(map)
        this.variables[v.name] = v
    }

    override public fun clone(): Variables {
        return Variables(variables)
    }
}