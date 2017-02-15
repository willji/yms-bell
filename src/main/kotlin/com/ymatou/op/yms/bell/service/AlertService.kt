package com.ymatou.op.yms.bell.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.mq.AlertLevel
import com.ymatou.op.yms.bell.mq.AlertMessage
import com.ymatou.op.yms.bell.mq.SenderService
import org.slf4j.LoggerFactory
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

/**
 * Created by xuemingli on 16/9/7.
 */
class AlertService(val senderService: SenderService, val item: Item) {
    val logger = LoggerFactory.getLogger(AlertService::class.java)!!
    val mapper:ObjectMapper = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule()).build()

    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    private fun alert(level: AlertLevel,
                      id: String,
                      message: String,
                      timestamp: Long = System.currentTimeMillis(),
                      additional: Map<String, String> = mapOf()) {
        val msg = AlertMessage(item.id!!, level, id, message, timestamp, additional)
        senderService.sendRealMessage(msg)
    }

    fun ok(id: String, message: String, timestamp: Long = System.currentTimeMillis(), additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.OK, id, message, timestamp, additional)
    }

    fun info(id: String, message: String, timestamp: Long = System.currentTimeMillis(), additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.INFO, id, message, timestamp, additional)
    }

    fun warning(id: String, message: String, timestamp: Long = System.currentTimeMillis(), additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.WARNING, id, message, timestamp, additional)
    }

    fun critical(id: String, message: String, timestamp: Long = System.currentTimeMillis(), additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.CRITICAL, id, message, timestamp, additional)
    }

    fun ok(id: String, message: String, additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.OK, id, message, System.currentTimeMillis(), additional)
    }

    fun info(id: String, message: String, additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.INFO, id, message, System.currentTimeMillis(), additional)
    }

    fun warning(id: String, message: String, additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.WARNING, id, message, System.currentTimeMillis(), additional)
    }

    fun critical(id: String, message: String, additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.CRITICAL, id, message, System.currentTimeMillis(), additional)
    }

    fun ok(id: String, message: String) {
        alert(AlertLevel.OK, id, message, System.currentTimeMillis(), mapOf())
    }

    fun info(id: String, message: String) {
        alert(AlertLevel.INFO, id, message, System.currentTimeMillis(), mapOf())
    }

    fun warning(id: String, message: String) {
        alert(AlertLevel.WARNING, id, message, System.currentTimeMillis(), mapOf())
    }

    fun critical(id: String, message: String) {
        alert(AlertLevel.CRITICAL, id, message, System.currentTimeMillis(), mapOf())
    }

    fun ok(id: String, message: String, timestamp: Long = System.currentTimeMillis()) {
        alert(AlertLevel.OK, id, message, timestamp, mapOf())
    }

    fun info(id: String, message: String, timestamp: Long = System.currentTimeMillis()) {
        alert(AlertLevel.INFO, id, message, timestamp, mapOf())
    }

    fun warning(id: String, message: String, timestamp: Long = System.currentTimeMillis()) {
        alert(AlertLevel.WARNING, id, message, timestamp, mapOf())
    }

    fun critical(id: String, message: String, timestamp: Long = System.currentTimeMillis()) {
        alert(AlertLevel.CRITICAL, id, message, timestamp, mapOf())
    }

    fun ok(message: String) {
        alert(AlertLevel.OK, item.id.toString(), message, System.currentTimeMillis(), mapOf())
    }

    fun info(message: String) {
        alert(AlertLevel.INFO, item.id.toString(), message, System.currentTimeMillis(), mapOf())
    }

    fun warning(message: String) {
        alert(AlertLevel.WARNING, item.id.toString(), message, System.currentTimeMillis(), mapOf())
    }

    fun critical(message: String) {
        alert(AlertLevel.CRITICAL, item.id.toString(), message, System.currentTimeMillis(), mapOf())
    }

    fun ok(message: String, timestamp: Long = System.currentTimeMillis()) {
        alert(AlertLevel.OK, item.id.toString(), message, timestamp, mapOf())
    }

    fun info(message: String, timestamp: Long = System.currentTimeMillis()) {
        alert(AlertLevel.INFO, item.id.toString(), message, timestamp, mapOf())
    }

    fun warning(message: String, timestamp: Long = System.currentTimeMillis()) {
        alert(AlertLevel.WARNING, item.id.toString(), message, timestamp, mapOf())
    }

    fun critical(message: String, timestamp: Long = System.currentTimeMillis()) {
        alert(AlertLevel.CRITICAL, item.id.toString(), message, timestamp, mapOf())
    }

    fun ok(message: String, timestamp: Long = System.currentTimeMillis(), additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.OK, item.id.toString(), message, timestamp, additional)
    }

    fun info(message: String, timestamp: Long = System.currentTimeMillis(), additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.INFO, item.id.toString(), message, timestamp, additional)
    }

    fun warning(message: String, timestamp: Long = System.currentTimeMillis(), additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.WARNING, item.id.toString(), message, timestamp, additional)
    }

    fun critical(message: String, timestamp: Long = System.currentTimeMillis(), additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.CRITICAL, item.id.toString(), message, timestamp, additional)
    }

    fun ok( message: String, additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.OK, item.id.toString(), message, System.currentTimeMillis(), additional)
    }

    fun info(message: String, additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.INFO, item.id.toString(), message, System.currentTimeMillis(), additional)
    }

    fun warning(message: String, additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.WARNING, item.id.toString(), message, System.currentTimeMillis(), additional)
    }

    fun critical(message: String, additional: Map<String, String> = mapOf()) {
        alert(AlertLevel.CRITICAL, item.id.toString(), message, System.currentTimeMillis(), additional)
    }
}