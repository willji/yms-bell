package com.ymatou.op.yms.bell.service

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.domain.Template
import com.ymatou.op.yms.bell.mq.SenderService
import com.ymatou.op.yms.bell.query.QueryNode
import com.ymatou.op.yms.bell.template.BaseTask
import com.ymatou.op.yms.bell.template.Variable
import com.ymatou.op.yms.bell.template.Variables
import groovy.lang.GroovyClassLoader
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * Created by xuemingli on 16/9/27.
 */
@Service
class ExecuteService {
    private val logger = LoggerFactory.getLogger(javaClass)!!
    private val cache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(CacheLoader.from { id: Long? -> load(id) })

    @Autowired
    private lateinit var senderService: SenderService

    @Autowired
    private lateinit var templateService: TemplateService

    @Autowired
    private lateinit var itemService: ItemService

    @Value("\${influxdb.read.url}")
    private lateinit var url: String

    @Value("\${influxdb.read.user}")
    private lateinit var user: String

    @Value("\${influxdb.read.password}")
    private lateinit var password: String

    @Value("\${influxdb.read.default.database}")
    private lateinit var database: String


    fun parse(script: String): Class<Any> {
        return GroovyClassLoader().parseClass(script)
    }

    fun load(id: Long?): Class<Any>? {
        val template = templateService.get(id!!) ?: return null
        return parse(template.script)
    }

    fun newInstance(cls: Class<Any>): BaseTask {
        val obj = cls.newInstance()
        return BaseTask::class.java.cast(obj)
    }

    fun execute(item: Item) {
        logger.info("execute ${item.name}<${item.id}>")
        val instance = newInstance(cache.get(item.template!!.id))
        val db = InfluxDBFactory.connect(url, user, password)
        db.setLogLevel(InfluxDB.LogLevel.NONE)
        instance.db = QueryNode(db, database)
        instance.alertor = AlertService(senderService, item)
        instance.item = item
        instance.vars = Variables()
        instance.service = itemService
        instance.setup()
        instance.vars.fill(item.variables)
        try {
            instance.run()
        } finally {
            instance.cleanup()
        }
    }

    fun refresh(id: Long) {
        cache.refresh(id)
    }

    fun variables(item: Item): Variables {
        val vars = variables(item.template!!)
        vars.fill(item.variables)
        return vars
    }

    fun variables(template: Template): Variables {
        val instance = newInstance(cache.get(template.id))
        instance.vars = Variables()
        instance.setup()
        val vars = instance.vars.clone()
        instance.cleanup()
        return vars
    }
}