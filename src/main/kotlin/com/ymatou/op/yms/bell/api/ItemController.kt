package com.ymatou.op.yms.bell.api

import com.ymatou.op.yms.bell.domain.Api
import com.ymatou.op.yms.bell.domain.App
import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.exception.NotFoundException
import com.ymatou.op.yms.bell.service.ExecuteService
import com.ymatou.op.yms.bell.service.ItemService
import com.ymatou.op.yms.bell.service.UserService
import com.ymatou.op.yms.bell.template.Variable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Created by xuemingli on 16/9/9.
 */
@RestController
@RequestMapping("/v1/item")
open class ItemController {
    @Autowired
    lateinit var itemService: ItemService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var executeService: ExecuteService

    private fun wrapItem(item: Item): Item {
        item.templateId = item.template!!.id!!
        item.templateName = item.template!!.name
        return item
    }

    @PostMapping("")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun create(@RequestBody item: Item): Item {
        return itemService.create(item)
    }

    @GetMapping("/{id}")
    @ResponseBody
    open fun find(@PathVariable id: Long): Item {
        return wrapItem(itemService.get(id) ?: throw NotFoundException("monitor item $id not fund"))
    }

    @PutMapping("/{id}")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun update(@RequestBody item: Item, @PathVariable("id") id: Long): Item {
        item.id = id
        return wrapItem(itemService.update(item))
    }

    @PutMapping("/{id}/enable")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun enable(@PathVariable id: Long): Item {
        return wrapItem(itemService.enable(id))
    }

    @PutMapping("/{id}/disable")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun disable(@PathVariable id: Long): Item {
        return wrapItem(itemService.disable(id))
    }


    @PutMapping("/{id}/subscribe")
    @ResponseBody
    open fun subscribe(@PathVariable id: Long,
                  @RequestParam(required = false, defaultValue = "1") page: Int,
                  @RequestParam(required = false, defaultValue = "50") size: Int): PagedListResponse<Item, Item> {
        itemService.subscribe(id)
        return PagedListResponse<Item, Item>(page, userService.getMySubscriptions(page, size)).map { wrapItem(it) }
    }

    @PutMapping("/{id}/unsubscribe")
    @ResponseBody
    open fun unsubscribe(@PathVariable id: Long,
                    @RequestParam(required = false, defaultValue = "1") page: Int,
                    @RequestParam(required = false, defaultValue = "50") size: Int): PagedListResponse<Item, Item> {
        itemService.unsubscribe(id)
        return PagedListResponse<Item, Item>(page, userService.getMySubscriptions(page, size)).map { wrapItem(it) }
    }

    @GetMapping("")
    @ResponseBody
    open fun list(@RequestParam(required = false, defaultValue = "1") page: Int,
             @RequestParam(required = false, defaultValue = "50") size: Int): PagedListResponse<Item, Item> {
        return PagedListResponse<Item, Item>(page, itemService.getAll(page, size)).map { wrapItem(it) }
    }

    @GetMapping("/search")
    @ResponseBody
    open fun search(@RequestParam("q") q: String): List<Item> {
        return itemService.search(q).map { wrapItem(it) }
    }

    @GetMapping("/{id}/execute")
    @ResponseBody
    open fun execute(@PathVariable id: Long) {
        val item = itemService.get(id)
        item ?: return
        executeService.execute(item)
    }

    @PutMapping("/{id}/app")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun addApp(@PathVariable id: Long, @RequestParam("name") name: String): Item {
        return wrapItem(itemService.addApp(id, name) ?: throw NotFoundException("item $id not found"))
    }

    @DeleteMapping("/{id}/app")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun removeApp(@PathVariable id: Long, @RequestParam("name") name: String): Item {
        return wrapItem(itemService.removeApp(id, name) ?: throw NotFoundException("item $id not found"))
    }

    @PutMapping("/{id}/api")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun addApi(@PathVariable id: Long,
               @RequestParam("app") app: String,
               @RequestParam("api") api: String): Item {
        return wrapItem(itemService.addApi(id, app, api) ?: throw NotFoundException("item $id not found"))
    }

    @DeleteMapping("/{id}/api")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun removeApi(@PathVariable id: Long,
                  @RequestParam("app") app: String,
                  @RequestParam("api") api: String): Item {
        return wrapItem(itemService.removeApi(id, app, api) ?: throw NotFoundException("item $id not found"))
    }

    @GetMapping("/{id}/vars")
    @ResponseBody
    open fun getVars(@PathVariable id: Long): Map<String, Variable> {
        val item = itemService.get(id) ?: throw NotFoundException("item $id not found")
        val vars = executeService.variables(item)
        return vars.get()
    }

    @GetMapping("/{id}/apps")
    @ResponseBody
    open fun getApps(@PathVariable id: Long,
                @RequestParam(required = false, defaultValue = "1") page: Int,
                @RequestParam(required = false, defaultValue = "50") size: Int): PagedListResponse<App, App> {
        return PagedListResponse<App, App>(page, itemService.getApps(id, page, size)).map {
            it.items = mutableSetOf()
            it
        }
    }

    @GetMapping("/{id}/apis")
    @ResponseBody
    open fun getApis(@PathVariable id: Long,
                @RequestParam("app") app: Long,
                @RequestParam(required = false, defaultValue = "1") page: Int,
                @RequestParam(required = false, defaultValue = "50") size: Int): PagedListResponse<Api, Api> {
        return PagedListResponse<Api, Api>(page, itemService.getApis(id, app, page, size)).map {
            it.items = mutableSetOf()
            it
        }
    }
}