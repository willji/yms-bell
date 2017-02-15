package com.ymatou.op.yms.bell.authentication

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

/**
 * Created by xuemingli on 16/9/9.
 */
@Component
open class LDAPAuthenticationProvider: AuthenticationProvider {
    @Autowired
    lateinit var authenticationService: AuthenticationService

    override fun authenticate(authentication: Authentication?): Authentication {
        val token = authentication as LDAPAuthenticationToken
        val user = authenticationService.getUserByToken(token.credentials)
        val authorities = user.roles.map { SimpleGrantedAuthority(it.name) }
        return LDAPAuthenticationToken(token.credentials, user, authorities.toMutableList())
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return LDAPAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}