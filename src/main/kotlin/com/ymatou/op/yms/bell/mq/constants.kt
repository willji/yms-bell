package com.ymatou.op.yms.bell.mq

/**
 * Created by xuemingli on 2016/10/26.
 */

enum class AlertLevel {
    OK,
    INFO,
    WARNING,
    CRITICAL,
}

object AMQPConfig {
    const val EXCHANGE = "yms2.direct"
    const val REAL = "yms2.real"
    const val BATCH = "yms2.batch"
}

data class AlertMessage(
        val item: Long,
        val level: AlertLevel,
        val id: String,
        val message: String,
        val timestamp: Long,
        val additional: Map<String, String> = mapOf()
)

data class BatchAlertMessage(
        val item: Long,
        val recipient: String,
        val count: Long,
        val timestamp: Long
)