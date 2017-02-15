package com.ymatou.op.yms.bell.api

import com.ymatou.op.yms.bell.authentication.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriUtils

/**
 * Created by xuemingli on 16/9/9.
 */
@RestController
@RequestMapping("/v1/authentication", produces = arrayOf("application/json"))
class AuthenticationController() {
    val logger = LoggerFactory.getLogger(javaClass)!!

    @Autowired
    lateinit var authenticationService: AuthenticationService


    @PostMapping("/login")
    @ResponseBody
    fun login(@RequestBody login: Login): LoginResult {
        val (token, user, expiration) = authenticationService.authenticate(login)
        val redirect = login.redirect?: "/"
        if (user.isNew) {
            return LoginResult(next = "/profile?next=${UriUtils.encode(redirect, "UTF-8")}", token = token, user = user, expiration = expiration)
        }
        return LoginResult(next = redirect, token = token, user = user, expiration = expiration)
    }
}