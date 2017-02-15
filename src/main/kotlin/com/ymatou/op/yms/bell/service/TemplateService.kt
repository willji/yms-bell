package com.ymatou.op.yms.bell.service

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.PagedList
import com.ymatou.op.yms.bell.authentication.SecurityService
import com.ymatou.op.yms.bell.domain.Template
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.naming.OperationNotSupportedException

/**
 * Created by xuemingli on 2016/10/13.
 */
@Service
class TemplateService {
    private val logger = LoggerFactory.getLogger(javaClass)!!

    @Autowired
    private lateinit var server: EbeanServer

    @Autowired
    private lateinit var executeService: ExecuteService

    @Autowired
    private lateinit var securityService: SecurityService

    fun create(template: Template): Template {
        executeService.parse(template.script)
        template.creator = securityService.getPrincipal()
        server.save(template)
        logger.info("template ${template.name} created")
        return template
    }

    fun get(id: Long): Template? {
        return server.find(Template::class.java, id)
    }

    fun update(template: Template): Template {
        executeService.parse(template.script)
        template.creator = securityService.getPrincipal()
        server.update(template)
        executeService.refresh(template.id!!)
        logger.info("template ${template.name} updated")
        return template
    }

    fun delete(id: Long): Template? {
        val template = get(id) ?: return null
        if (template.items.size > 0) {
            throw OperationNotSupportedException("template reference not null, can not delete")
        }
        server.delete(template)
        return template
    }

    fun getAll(page: Int, size: Int): PagedList<Template> {
        return server.find(Template::class.java).setFirstRow((page-1)*size).setMaxRows(size).findPagedList()
    }

    fun search(q: String): List<Template> {
        return server.find(Template::class.java).where().ilike("name", "%$q%").setMaxRows(20).findList()
    }

    fun findByName(name: String): Template? {
        return server.find(Template::class.java).where().eq("name", name).findUnique()
    }
}