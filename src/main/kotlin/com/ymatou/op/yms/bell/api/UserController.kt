package com.ymatou.op.yms.bell.api

import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.service.ItemService
import com.ymatou.op.yms.bell.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by xuemingli on 16/9/12.
 */
@RestController
@RequestMapping("/v1/user")
@ResponseBody
class UserController {
    @Autowired
    lateinit var itemService: ItemService

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/subscriptions")
    fun getSubscription(@RequestParam(required = false, defaultValue = "1") page: Int,
                        @RequestParam(required = false, defaultValue = "50") size: Int):PagedListResponse<Item, Item> {
        return PagedListResponse(page, userService.getMySubscriptions(page, size))
    }
}