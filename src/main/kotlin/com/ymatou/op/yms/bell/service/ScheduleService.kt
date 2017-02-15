package com.ymatou.op.yms.bell.service

import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.job.BatchSendJob
import com.ymatou.op.yms.bell.job.InfluxDBQueryJob
import com.ymatou.op.yms.bell.job.SyncAppsJob
import com.ymatou.op.yms.bell.job.SyncUsersJob
import org.quartz.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PreDestroy

/**
 * Created by xuemingli on 16/9/7.
 */
@Service
class ScheduleService {
    @Autowired
    private lateinit var scheduler: Scheduler

    fun schedule(item: Item, reSchedule: Boolean = true) {
        scheduleItem(item, reSchedule)
        scheduleBatchSend(item, reSchedule)
    }

    fun unschedule(item: Item) {
        unscheduleItem(item)
        unscheduleBatchSend(item)
    }

    fun scheduleItem(item: Item, reSchedule: Boolean = true) {
        if (reSchedule) {
            unscheduleItem(item)
        }
        if (!item.enable) {
            return
        }
        val job = JobBuilder.newJob(InfluxDBQueryJob::class.java)
                .withIdentity(item.id.toString(), "item_check")
                .storeDurably(true)
                .build()
        val trigger = TriggerBuilder.newTrigger()
                .withIdentity(item.id.toString(), "item_check")
                .withSchedule(CronScheduleBuilder.cronSchedule(item.cron))
                .build()
        if (!scheduler.checkExists(TriggerKey.triggerKey(item.id.toString(), "item_check"))) {
            scheduler.scheduleJob(job, trigger)
        }
    }

    fun unscheduleItem(item: Item) {
        if (scheduler.checkExists(TriggerKey.triggerKey(item.id.toString(), "item_check"))) {
            scheduler.unscheduleJob(TriggerKey.triggerKey(item.id.toString(), "item_check"))
        }

        if (scheduler.checkExists(JobKey.jobKey(item.id.toString(), "item_check"))) {
            scheduler.deleteJob(JobKey.jobKey(item.id.toString(), "item_check"))
        }
    }

    fun scheduleBatchSend(item: Item, reSchedule: Boolean = true) {
        if (reSchedule) {
            unscheduleBatchSend(item)
        }

        if (item.convergence <= 0) {
            return
        }
        val job = JobBuilder.newJob(BatchSendJob::class.java)
                .withIdentity(item.id.toString(), "batch_send")
                .storeDurably(true)
                .build()
        val trigger = TriggerBuilder.newTrigger()
                .withIdentity(item.id.toString(), "batch_send")
                .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(item.convergence))
                .build()
        if (!scheduler.checkExists(TriggerKey.triggerKey(item.id.toString(), "batch_send"))) {
            scheduler.scheduleJob(job, trigger)
        }
    }

    fun unscheduleBatchSend(item: Item) {
        if (scheduler.checkExists(TriggerKey.triggerKey(item.id.toString(), "batch_send"))) {
            scheduler.unscheduleJob(TriggerKey.triggerKey(item.id.toString(), "batch_send"))
        }
        if (scheduler.checkExists(JobKey.jobKey(item.id.toString(), "batch_send"))) {
            scheduler.deleteJob(JobKey.jobKey(item.id.toString(), "batch_send"))
        }
    }

    fun syncApp(expression: String) {
        val id = "app"
        val job = JobBuilder.newJob(SyncAppsJob::class.java)
                .withIdentity(id, "sync")
                .storeDurably(true).build()
        val trigger = TriggerBuilder.newTrigger()
                .withIdentity(id, "sync")
                .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                .build()
        if (scheduler.checkExists(TriggerKey.triggerKey(id, "sync"))) {
            scheduler.unscheduleJob(TriggerKey.triggerKey(id, "sync"))
        }
        if (scheduler.checkExists(JobKey.jobKey(id, "sync"))) {
            scheduler.deleteJob(JobKey.jobKey(id, "sync"))
        }
        scheduler.scheduleJob(job, trigger)
    }

    fun syncUsers(expression: String) {
        val id = "user"
        val job = JobBuilder.newJob(SyncUsersJob::class.java)
                .withIdentity(id, "sync")
                .storeDurably(true)
                .build()
        val trigger = TriggerBuilder.newTrigger()
                .withIdentity(id, "sync")
                .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                .build()
        if (scheduler.checkExists(TriggerKey.triggerKey(id, "sync"))) {
            scheduler.unscheduleJob(TriggerKey.triggerKey(id, "sync"))
            scheduler.deleteJob(JobKey.jobKey(id, "sync"))
        }
        scheduler.scheduleJob(job, trigger)
    }

    @PreDestroy
    fun destroy() {
        scheduler.shutdown(true)
    }


    fun clean() {
        scheduler.clear()
    }
}