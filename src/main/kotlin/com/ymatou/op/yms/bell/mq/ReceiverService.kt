package com.ymatou.op.yms.bell.mq

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.ymatou.op.yms.bell.mq.AMQPConfig.BATCH
import com.ymatou.op.yms.bell.mq.AMQPConfig.REAL
import com.ymatou.op.yms.bell.service.SendService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by xuemingli on 2016/10/26.
 */
@Service
class ReceiverService {
    private val mapper = ObjectMapper().registerModule(KotlinModule())
    private val logger = LoggerFactory.getLogger(javaClass)!!

    @Autowired
    private lateinit var sender: SendService

    @RabbitListener(queues = arrayOf(REAL))
    fun sendRealMessage(message: String) {
        try {
            sender.send(message = mapper.readValue(message))
        } catch (e: Exception) {
            logger.error("send message $message error: ${e.message}")
        }
    }

    @RabbitListener(queues = arrayOf(BATCH))
    fun sendBatchMessage(message: String) {
        try {
            sender.batchSend(message = mapper.readValue(message))
        } catch (e: Exception) {
            logger.error("send message $message error: ${e.message}")
        }

    }
}