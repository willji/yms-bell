package com.ymatou.op.yms.bell.authentication

import com.ymatou.op.yms.bell.domain.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

/**
 * Created by xuemingli on 16/9/9.
 */
@Service
class SecurityService {
    fun getPrincipal(): User {
        return SecurityContextHolder.getContext().authentication.principal as User
    }

    fun hasRole(vararg roles: String): Boolean {
        val mine = getPrincipal().roles
                .map { it.name.toLowerCase() }
                .toMutableSet()
        mine.retainAll(roles)
        return mine.size > 0
    }

    fun getCredentials(): String {
        return SecurityContextHolder.getContext().authentication.credentials as String
    }
}