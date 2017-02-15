package com.ymatou.op.yms.bell.spring

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.EbeanServerFactory
import com.avaje.ebean.config.ServerConfig
import com.avaje.ebean.springsupport.txn.SpringAwareJdbcTransactionManager
import com.ymatou.op.yms.bell.authentication.SecurityService
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.sql.DataSource

/**
 * Created by xuemingli on 16/9/8.
 */
@Component
class EbeanFactoryBean: FactoryBean<EbeanServer> {
    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var securityService: SecurityService

    @Autowired
    private lateinit var env: Environment

    override fun isSingleton(): Boolean {
        return true
    }

    override fun getObject(): EbeanServer {
        val config = ServerConfig()
        config.name = "db"
        config.isDdlGenerate = env.getProperty("ebean.ddl.generate", Boolean::class.java)
        config.isDdlRun = env.getProperty("ebean.ddl.run", Boolean::class.java)
        config.isDdlCreateOnly = false
        config.dataSource = dataSource
        config.isDefaultServer = true
        config.isRegister = true
        config.externalTransactionManager = SpringAwareJdbcTransactionManager()
        config.packages = listOf("com.ymatou.op.yms.bell.domain")
        config.setCurrentUserProvider {
            securityService.getPrincipal()
        }
        return EbeanServerFactory.create(config)
    }

    override fun getObjectType(): Class<*> {
        return EbeanServer::class.java
    }
}