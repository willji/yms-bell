package com.ymatou.op.yms.bell.api

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Created by xuemingli on 16/9/12.
 */
@Controller
@RequestMapping("", produces = arrayOf("text/html"))
class IndexController {
    @GetMapping("/ui/**")
    @ResponseBody
    fun index(): String {
        return javaClass.getResourceAsStream("/index.html").buffered().reader().use { it.readText() }
    }

    @GetMapping("/")
    fun redirect(): String {
        return "redirect:/ui"
    }
}