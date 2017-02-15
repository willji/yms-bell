package com.ymatou.op.yms.bell.service

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.PagedList
import com.ymatou.op.yms.bell.authentication.SecurityService
import com.ymatou.op.yms.bell.domain.Api
import com.ymatou.op.yms.bell.domain.App
import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.exception.NotFoundException
import com.ymatou.op.yms.bell.toHexString
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.MessageDigest

/**
 * Created by xuemingli on 16/9/9.
 */
@Service
class ItemService {
    private val logger = LoggerFactory.getLogger(javaClass)!!
    private val md = MessageDigest.getInstance("SHA-1")

    @Autowired
    private lateinit var server: EbeanServer

    @Autowired
    private lateinit var scheduleService: ScheduleService

    @Autowired
    private lateinit var securityService: SecurityService

    @Autowired
    private lateinit var templateService: TemplateService

    @Autowired
    private lateinit var appService: AppService


    fun create(item: Item): Item {
        item.template = templateService.findByName(item.templateName)!!
        item.creator = securityService.getPrincipal()
        item.modifier = securityService.getPrincipal()
        item.subscribers.add(securityService.getPrincipal())
        server.save(item)
        scheduleService.schedule(item)
        return item
    }

    fun update(item: Item): Item {
        val origin = get(item.id!!) ?: throw NotFoundException("item ${item.id} not found")
        origin.name = item.name
        origin.modifier = securityService.getPrincipal()
        origin.template = templateService.findByName(item.templateName)!!
        origin.subscribers.add(securityService.getPrincipal())
        origin.variables = item.variables
        origin.cron = item.cron
        origin.descriptor = item.descriptor
        origin.enable = item.enable
        server.update(origin)
        scheduleService.schedule(item)
        return origin
    }

    fun delete(id: Long): Item? {
        val item = get(id) ?: return null
        server.delete(item)
        scheduleService.unschedule(item)
        return item
    }

    fun enable(id: Long): Item {
        val item = server.find(Item::class.java, id) ?: throw NotFoundException("monitor item $id not found")
        item.enable = true
        item.subscribers.add(securityService.getPrincipal())
        server.save(item)
        scheduleService.schedule(item)
        return item
    }

    fun disable(id: Long): Item {
        val item = server.find(Item::class.java, id) ?: throw NotFoundException("monitor item $id not found")
        item.enable = false
        item.subscribers.add(securityService.getPrincipal())
        server.save(item)
        scheduleService.schedule(item)
        return item
    }

    fun get(id: Long): Item? {
        val item = server.find(Item::class.java, id) ?: return null
        return item
    }

    fun find(name: String): Item? {
        return server.find(Item::class.java)
                .where().eq("name", name).findUnique()
    }

    fun subscribe(id: Long): Item {
        val item = server.find(Item::class.java, id) ?: throw NotFoundException("monitor item $id not found")
        item.subscribers.add(securityService.getPrincipal())
        server.save(item)
        return item
    }

    fun unsubscribe(id: Long): Item {
        val item = server.find(Item::class.java, id) ?: throw NotFoundException("monitor item $id not found")
        item.subscribers.remove(securityService.getPrincipal())
        server.save(item)
        return item
    }

    fun getAll(page: Int, size: Int): PagedList<Item> {
        return server.find(Item::class.java)
                .setFirstRow((page-1) * size).setMaxRows(size).findPagedList()
    }

    fun addApp(id: Long, name: String): Item? {
        val item = get(id) ?: return null
        val app = appService.get(name) ?: return item
        app.items.add(item)
        server.save(app)
        return item
    }

    fun removeApp(id: Long, name: String): Item? {
        val item = get(id) ?: return null
        val app = appService.get(name) ?: return item
        app.items.remove(item)
        appService.getApiSetByAppAndItems(app, item).forEach {
            it.items.remove(item)
            server.save(it)
        }
        server.save(app)
        return item
    }

    fun addApi(id: Long, appName: String, apiName: String): Item? {
        val item = get(id) ?: return null
        val api = appService.getApi(appName, apiName) ?: return item
        api.app.items.add(item)
        api.items.add(item)
        server.save(api)
        return item
    }

    fun removeApi(id: Long, appName: String, apiName: String): Item? {
        val item = get(id) ?: return null
        val api = appService.getApi(appName, apiName) ?: return item
        api.items.remove(item)
        server.save(api)
        if (!appService.itemHasOtherApi(api, item)) {
            removeApp(item.id!!, api.app.name)
        }
        return item
    }

    fun match(item: Item, appName: String? = null, apiName: String? = null): Boolean {
        if (appName == null) {
            return true
        }
        val app = server.find(App::class.java).where().eq("name", appName).and().eq("items.id", item.id).findUnique()

        if (app != null) {
            if (apiName == null) {
                return true
            }
            val hash = md.digest(apiName.toByteArray()).toHexString()
            val api = server.find(Api::class.java).where().eq("hash", hash).and().eq("app", app).and().eq("items.id", item.id).findUnique()
            if (api != null) {
                return true
            }
//            val count = server.find(Api::class.java).where().eq("hash", hash).and().eq("app", app).findUnique()?.items?.size ?: 0
//            if (count <= 0) {
//                return true
//            }
        }
//        val count = server.find(App::class.java).select("id").where().eq("name", appName).findCount()
//        if (count <= 0) {
//            return true
//        }
        return false
    }

    fun getApps(id: Long, page: Int, size: Int): PagedList<App> {
        return server.find(App::class.java)
                .where()
                .eq("items.id", id)
                .setFirstRow((page - 1) * size)
                .setMaxRows(size)
                .findPagedList()
    }

    fun getApis(id: Long, app: Long, page: Int, size: Int): PagedList<Api> {
        return server.find(Api::class.java)
                .select("id, name")
                .where()
                .eq("items.id", id)
                .and()
                .eq("app", appService.get(app))
                .setFirstRow((page - 1) * size)
                .setMaxRows(size)
                .findPagedList()
    }

    fun connect(itemId: Long, appName: String, apiName: String): Api? {
        addApp(itemId, appName) ?: return null
        addApi(itemId, appName, apiName) ?: return null
        return appService.getApi(appName, apiName)
    }

    fun disconnect(itemId: Long, appName: String, apiName: String): Api? {
        removeApi(itemId, appName, apiName)
        return appService.getApi(appName, apiName)
    }

    fun search(q: String): List<Item> {
        return server.find(Item::class.java).where().ilike("name", "%$q%").setMaxRows(20).findList()
    }
}