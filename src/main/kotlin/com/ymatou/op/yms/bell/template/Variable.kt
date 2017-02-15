package com.ymatou.op.yms.bell.template

/**
 * Created by xuemingli on 2016/10/13.
 */
data class Variable(
        val name: String,
        val display: String,
        val type: VariableType,
        val required: Boolean = true,
        val defaultValue: Any? = null,
        val desc: String,
        var value: Any? = null) {
    companion object {
        fun create(map: Map<String, Any?>): Variable {
            val name = map["name"]!! as String
            val display = (map["display"] ?: name) as String
            var type = VariableType.STRING
            if (map["type"] is Int) {
                for (v in VariableType.values()) {
                    if (v.ordinal == map["type"]) {
                        type = v
                        break
                    }
                }
            }
            if (map["type"] is String) {
                type = VariableType.valueOf(map["type"] as String)
            }

            if (map["type"] is VariableType) {
                type = map["type"] as VariableType
            }
            val required = (map["required"] ?: true) as Boolean
            val defaultValue = map["defaultValue"] ?: null
            val desc = (map["desc"] ?: display) as String
            return Variable(name, display, type, required, defaultValue, desc)
        }
    }

    fun set(value: Any?): Variable {
        var _value = value
        if (value == null) {
            if (required) {
                if (defaultValue == null) {
                    throw RuntimeException("$name is required and not have default value")
                } else {
                    _value = defaultValue
                }
            } else {
                return this
            }
        }
        val exc = TypeCastException("$name require ${type.name}, but ${value?.javaClass?.name}")
        when(type) {
            VariableType.INT ->
                if (_value is Int)
                    this.value = _value.toLong()
                else if (_value is Long)
                    this.value = _value
                else
                    throw exc
            VariableType.FLOAT ->
                if (_value is Float)
                    this.value = _value.toDouble()
                else if (_value is Double)
                    this.value = _value
                else if (_value is Int)
                    this.value = _value.toDouble()
                else if (_value is Long)
                    this.value = _value.toDouble()
                else
                    throw exc
            VariableType.STRING ->
                if (_value is String)
                    this.value = _value
                else
                    throw exc
        }
        return this
    }
}