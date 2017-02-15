package com.ymatou.op.yms.bell.exception

import org.springframework.security.core.AuthenticationException

/**
 * Created by xuemingli on 16/9/9.
 */
class LDAPAuthenticationException: AuthenticationException {
    constructor(msg: String): super(msg){}

    constructor(msg: String, t:Throwable): super(msg, t) {}
}