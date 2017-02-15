package com.ymatou.op.yms.bell.job

import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.service.ExecuteService
import com.ymatou.op.yms.bell.service.ItemService
import groovy.lang.Script
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by xuemingli on 16/9/7.
 */
class InfluxDBQueryJob : Job {
    val logger = LoggerFactory.getLogger(javaClass)!!
    val cache = ConcurrentHashMap<Long, Script>()

    @Autowired
    lateinit var executeService: ExecuteService

    @Autowired
    lateinit var itemService: ItemService


    override fun execute(context: JobExecutionContext?) {
        context ?: return
        val jobId = context.jobDetail.key.name
        val item: Item?
        try {
            item = itemService.get(jobId?.toLong()!!)
        } catch (e: Exception) {
            logger.error("get item<$jobId> error: ", e)
            return
        }
        if (item != null) {
            executeService.execute(item)
        } else {
            logger.error("item $jobId not found")
        }

    }
}