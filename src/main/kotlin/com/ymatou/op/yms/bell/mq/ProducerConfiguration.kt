package com.ymatou.op.yms.bell.mq

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter

/**
 * Created by xuemingli on 2016/10/26.
 */

@Configuration
open class ProducerConfiguration {
    @Bean
    open fun rabbitAdmin(connectionFactory: ConnectionFactory): RabbitAdmin {
        return RabbitAdmin(connectionFactory)
    }

    @Bean
    open fun exchange(rabbitAdmin: RabbitAdmin): Exchange {
        val exchange =  DirectExchange(AMQPConfig.EXCHANGE)
        rabbitAdmin.declareExchange(exchange)
        return exchange
    }

    @Bean
    open fun realQueue(rabbitAdmin: RabbitAdmin): Queue {
        val queue = Queue(AMQPConfig.REAL, true)
        rabbitAdmin.declareQueue(queue)
        return queue
    }

    @Bean
    open fun batchSendQueue(rabbitAdmin: RabbitAdmin): Queue {
        val queue = Queue(AMQPConfig.BATCH, true)
        rabbitAdmin.declareQueue(queue)
        return queue
    }

    @Bean
    open fun realBinding(rabbitAdmin: RabbitAdmin): Binding {
        val binding = BindingBuilder.bind(realQueue(rabbitAdmin)).to(exchange(rabbitAdmin)).with(AMQPConfig.REAL).noargs()
        rabbitAdmin.declareBinding(binding)
        return binding
    }

    @Bean
    open fun batchSendBinding(rabbitAdmin: RabbitAdmin): Binding {
        val binding = BindingBuilder.bind(batchSendQueue(rabbitAdmin)).to(exchange(rabbitAdmin)).with(AMQPConfig.BATCH).noargs()
        rabbitAdmin.declareBinding(binding)
        return binding
    }

    @Bean
    open fun jsonConverter(): MappingJackson2MessageConverter {
        return MappingJackson2MessageConverter()
    }

    @Bean
    open fun rabbitMessagingTemplate(rabbitTemplate: RabbitTemplate): RabbitMessagingTemplate {
        val rabbitMessagingTemplate = RabbitMessagingTemplate()
        rabbitMessagingTemplate.messageConverter = jsonConverter()
        rabbitMessagingTemplate.rabbitTemplate = rabbitTemplate
        return rabbitMessagingTemplate
    }
}