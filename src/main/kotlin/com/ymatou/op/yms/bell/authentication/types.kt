package com.ymatou.op.yms.bell.authentication

import com.ymatou.op.yms.bell.domain.User

/**
 * Created by xuemingli on 16/9/9.
 */
data class Login(val username: String, val password: String, val redirect: String?)
data class AuthenticationResult(val token: String, val user: User, val expiration: Long)
data class GrantResult(val code: Int = 200, val token: String, val confirm: Boolean)
data class Token(val token: String, val expire: Long)
data class TokenResult(val access: Token, val refresh: Token)

data class LoginResult(val code: Int = 200, val next: String = "/", val token: String, val user: User, val expiration: Long)
data class LogoutResult(val code: Int = 200, val message: String = "succeed")