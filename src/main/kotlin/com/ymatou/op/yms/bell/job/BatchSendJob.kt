package com.ymatou.op.yms.bell.job

import com.ymatou.op.yms.bell.mq.BatchAlertMessage
import com.ymatou.op.yms.bell.mq.SenderService
import com.ymatou.op.yms.bell.service.*
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by xuemingli on 2016/10/26.
 */
class BatchSendJob: Job {
    private val logger = LoggerFactory.getLogger(javaClass)!!

    @Autowired
    private lateinit var historyService: HistoryService

    @Autowired
    private lateinit var itemService: ItemService

    @Autowired
    private lateinit var senderService: SenderService

    override fun execute(context: JobExecutionContext) {
        val timestamp = System.currentTimeMillis()
        val id = context.jobDetail.key.name.toLong()
        val item = itemService.get(id) ?: return
        val counter = historyService.getCount(item, timestamp)
        counter.filter { it.value > 0 }.forEach {
            val message = BatchAlertMessage(item.id!!, it.key, it.value, timestamp)
            logger.info("batch send message to ${message.recipient} for ${item.name}")
            senderService.sendBatchMessage(message)
        }

    }
}