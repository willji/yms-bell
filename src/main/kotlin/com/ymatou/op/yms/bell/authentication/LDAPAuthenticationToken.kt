package com.ymatou.op.yms.bell.authentication

import com.ymatou.op.yms.bell.domain.User
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * Created by xuemingli on 16/9/9.
 */
class LDAPAuthenticationToken(val token: String, val user: User? = null, authorities: MutableCollection<out GrantedAuthority>? = null): AbstractAuthenticationToken(authorities) {
    init {
        if (user == null) {
            super.setAuthenticated(false)
        } else {
            super.setAuthenticated(true)
        }
    }

    override fun getCredentials(): String {
        return token
    }

    override fun getPrincipal(): User? {
        return user
    }

    override fun setAuthenticated(authenticated: Boolean) {
        if(isAuthenticated) {
            throw IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead")
        } else {
            super.setAuthenticated(false)
        }
    }
}