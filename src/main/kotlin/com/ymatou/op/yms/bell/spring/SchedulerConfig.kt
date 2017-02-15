package com.ymatou.op.yms.bell.spring

import org.quartz.spi.JobFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import java.io.IOException
import java.util.*

/**
 * Created by xuemingli on 16/9/7.
 */
@Configuration
@ConditionalOnProperty(name = arrayOf("quartz.enabled"))
open class SchedulerConfig {
    @Autowired
    private lateinit var env: Environment

    @Bean
    open fun jobFactory(applicationContext: ApplicationContext): JobFactory {
        val jobFactory = AutowiringSpringBeanJobFactory()
        jobFactory.setApplicationContext(applicationContext)
        return jobFactory
    }

    @Bean
    @Throws(IOException::class)
    open fun schedulerFactoryBean(jobFactory: JobFactory): SchedulerFactoryBean {
        val factory = SchedulerFactoryBean()
        // this allows to update triggers in DB when updating settings in config file:
        factory.setOverwriteExistingJobs(true)
        factory.setJobFactory(jobFactory)
        factory.setQuartzProperties(quartzProperties())

        return factory
    }

    @Bean
    @Throws(IOException::class)
    open fun quartzProperties(): Properties {
        val propertiesFactoryBean = PropertiesFactoryBean()
        propertiesFactoryBean.setLocation(ClassPathResource(env.getProperty("quartz.properties", "/quartz.properties")))
        propertiesFactoryBean.afterPropertiesSet()
        return propertiesFactoryBean.`object`
    }
}