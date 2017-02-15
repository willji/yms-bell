package com.ymatou.op.yms.bell.api

import com.ymatou.op.yms.bell.domain.Api
import com.ymatou.op.yms.bell.domain.App
import com.ymatou.op.yms.bell.exception.NotFoundException
import com.ymatou.op.yms.bell.service.AppService
import com.ymatou.op.yms.bell.service.ItemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Created by xuemingli on 2016/10/17.
 */
@RestController
@RequestMapping("/v1/app")
open class AppController {

    @Autowired
    private lateinit var appService: AppService

    @Autowired
    private lateinit var itemService: ItemService


    @GetMapping("/search")
    @ResponseBody
    open fun searchApp(@RequestParam("q") q: String, @RequestParam("item") item: Long): List<String> {
        return appService.search(q, item)
    }

    @GetMapping("/api/search")
    @ResponseBody
    open fun searchApi(@RequestParam("q") q: String,
                  @RequestParam("app") app: String,
                  @RequestParam("item") item: Long): List<String> {
        return appService.searchApi(q, app, item)
    }

    @PutMapping("/api/item")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun addItem(@RequestParam("name") name: String,
                @RequestParam("app") app: String,
                @RequestParam("item") item: Long): Api {
        return itemService.connect(item, app, name) ?: throw NotFoundException("not found")
    }

    @DeleteMapping("/api/item")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun removeItem(@RequestParam("name") name: String,
                @RequestParam("app") app: String,
                @RequestParam("item") item: Long): Api {
        return itemService.disconnect(item, app, name) ?: throw NotFoundException("not found")
    }

    @GetMapping("/api")
    @ResponseBody
    open fun getApi(@RequestParam("name") name: String, @RequestParam("app") appName: String): Api {
        return appService.getApi(appName, name) ?: throw NotFoundException("not found api $name of app $appName")
    }

//    @GetMapping("/{name}")
//    @ResponseBody
//    fun getApp(@PathVariable("name") name: String): App {
//        return appService.get(name) ?: throw NotFoundException("app $name not found")
//    }
}