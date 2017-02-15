package com.ymatou.op.yms.bell.spring

import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.quartz.SpringBeanJobFactory

/**
 * Created by xuemingli on 16/9/7.
 */
class AutowiringSpringBeanJobFactory: SpringBeanJobFactory(), ApplicationContextAware {
    var beanFactory: AutowireCapableBeanFactory? = null

    override fun setApplicationContext(context: ApplicationContext?) {
        beanFactory = context?.autowireCapableBeanFactory
    }

    override fun createJobInstance(bundle: TriggerFiredBundle?): Any {
        val job = super.createJobInstance(bundle)
        beanFactory?.autowireBean(job)
        return job
    }
}