package com.ymatou.op.yms.bell

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.ymatou.op.yms.bell.service.AppService
import com.ymatou.op.yms.bell.service.ItemService
import com.ymatou.op.yms.bell.service.ScheduleService
import com.ymatou.op.yms.bell.spring.SchedulerConfig
import org.avaje.agentloader.AgentLoader
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import java.util.concurrent.Executors

@SpringBootApplication
@Import(value = SchedulerConfig::class)
open class BellApplication {
    @Autowired
    private lateinit var env: Environment

    @Autowired
    private lateinit var itemService: ItemService

    @Bean
    open fun objectMapper(): ObjectMapper {
        val mapper:ObjectMapper = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule()).build()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper
    }

    @Bean
    open fun ldapTemplate(): LdapTemplate {
        val ctx = LdapContextSource()
        ctx.setUrl(env.getRequiredProperty("ldap.url"))
        ctx.userDn = env.getRequiredProperty("ldap.user")
        ctx.password = env.getRequiredProperty("ldap.password")
        ctx.afterPropertiesSet()
        return LdapTemplate(ctx)
    }

    @Bean
    open fun schedule(scheduler: ScheduleService) = CommandLineRunner {
        //scheduler.clean()
        var page = 1
        val size = 500
        while (true) {
            val items = itemService.getAll(page, size)
            items.list.filter { it.enable }.forEach { scheduler.schedule(it, false) }
            if (!items.hasNext()) {
                break
            }
            page += 1
        }
        scheduler.syncApp(env.getProperty("job.sync.app", "0 15 */1 * * ?"))
        scheduler.syncUsers(env.getProperty("job.sync.user", "0 0 */1 * * ?"))
    }

}

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger(BellApplication::class.java)!!

    if (!AgentLoader.loadAgentFromClasspath("ebean-agent","debug=0;packages=com.ymatou.op.yms.bell.domain")) {
        logger.info("ebean-agent not found in classpath - not dynamically loaded")
    }
    SpringApplication.run(BellApplication::class.java, *args)
}
