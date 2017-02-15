package com.ymatou.op.yms.bell.mq

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by xuemingli on 2016/10/26.
 */
@Service
class SenderService {
    private val mapper = ObjectMapper().registerModule(KotlinModule())


    @Autowired
    private lateinit var rabbitTemplate: RabbitTemplate

    fun sendRealMessage(message: AlertMessage) {
        val data = mapper.writeValueAsString(message)
        rabbitTemplate.convertAndSend(AMQPConfig.EXCHANGE, AMQPConfig.REAL, data)
    }

    fun sendBatchMessage(message: BatchAlertMessage) {
        val data = mapper.writeValueAsString(message)
        rabbitTemplate.convertAndSend(AMQPConfig.EXCHANGE, AMQPConfig.BATCH, data)
    }
}