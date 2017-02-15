package com.ymatou.op.yms.bell.authentication

import com.ymatou.op.yms.bell.domain.User
import com.ymatou.op.yms.bell.exception.LDAPAuthenticationException
import com.ymatou.op.yms.bell.service.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.compression.CompressionCodecs
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.query.LdapQueryBuilder
import org.springframework.ldap.query.SearchScope
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by xuemingli on 16/9/9.
 */
@Service
class AuthenticationService(val ldapTemplate: LdapTemplate) {
    val logger = LoggerFactory.getLogger(javaClass)!!

    @Autowired
    lateinit var userService: UserService


    @Value("\${ldap.base}")
    lateinit var ldapBase: String

    @Value("\${authentication.sign.key}")
    lateinit var key: String

    @Value("\${authentication.session.expiration}")
    lateinit var expiration: String


    fun authenticate(login: Login): AuthenticationResult {
        val query = LdapQueryBuilder.query()
                .base(ldapBase)
                .searchScope(SearchScope.SUBTREE)
                .where("sAMAccountName").`is`(login.username)
        try {
            ldapTemplate.authenticate(query, login.password)
        } catch (e: EmptyResultDataAccessException) {
            logger.warn("${login.username} not exist")
            throw LDAPAuthenticationException("${login.username} not exist")
        } catch (e: Exception) {
            logger.error("Authentication Failed", e)
            throw LDAPAuthenticationException("Authentication Failed", e)
        }
        val user = getOrCreateUser(login)
        val expirationAt = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(expiration.toLong())
        val token = Jwts.builder()
                .setSubject(login.username)
                .setExpiration(Date(expirationAt))
                .signWith(SignatureAlgorithm.HS512, key)
                .compressWith(CompressionCodecs.GZIP)
                .compact()
        return AuthenticationResult(token, user, expirationAt)
    }


    private fun getOrCreateUser(login: Login): User {
        return userService.getByEmail(login.username) ?: userService.create(User(email = login.username, name = login.username.split('@')[0]))
    }

    fun getUserByToken(token: String): User {
        try {
            val email = Jwts.parser().setSigningKey(key).parseClaimsJws(token).body.subject
            return userService.getByEmail(email) ?: throw LDAPAuthenticationException("user $email not found")
        }catch (e: Exception) {
            throw LDAPAuthenticationException(e.message ?: "Not Authenticated")
        }
    }
}