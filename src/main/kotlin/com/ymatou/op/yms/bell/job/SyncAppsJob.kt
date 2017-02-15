package com.ymatou.op.yms.bell.job

import com.ymatou.op.yms.bell.service.AppService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by xuemingli on 2016/10/17.
 */
class SyncAppsJob: Job {
    @Autowired
    private lateinit var appService: AppService

    override fun execute(context: JobExecutionContext?) {
        appService.sync()
    }
}