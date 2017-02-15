package com.ymatou.op.yms.bell.mq

import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory

/**
 * Created by xuemingli on 2016/10/26.
 */
@Configuration
@EnableRabbit
open class ConsumerConfiguration: RabbitListenerConfigurer {
    @Bean
    open fun defaultMessageHandlerMethodFactory(): DefaultMessageHandlerMethodFactory {
        val factory = DefaultMessageHandlerMethodFactory()
        factory.setMessageConverter(MappingJackson2MessageConverter())
        return factory
    }

    @Bean
    open fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO)
        return factory
    }

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        registrar.messageHandlerMethodFactory = defaultMessageHandlerMethodFactory()
    }

}