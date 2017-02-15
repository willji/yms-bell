package com.ymatou.op.yms.bell.authentication

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by xuemingli on 16/9/9.
 */
class LDAPAuthenticationFilter: OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val token = request.getHeader("X-Authorization-Token")
        if (!token.isNullOrEmpty() && !request.servletPath.startsWith("/v1/authentication/login")) {
            SecurityContextHolder.getContext().authentication = LDAPAuthenticationToken(token!!)
        }
        filterChain.doFilter(request, response)
    }
}