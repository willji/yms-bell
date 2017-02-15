package com.ymatou.op.yms.bell.job

import com.ymatou.op.yms.bell.service.CmdbService
import com.ymatou.op.yms.bell.service.UserService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by xuemingli on 2016/10/22.
 */
class SyncUsersJob: Job {
    @Autowired
    private lateinit var cmdbService: CmdbService

    @Autowired
    private lateinit var userService: UserService

    override fun execute(context: JobExecutionContext?) {
        cmdbService.getAllUsers().forEach {
            userService.createOrUpdate(it)
        }
    }

}