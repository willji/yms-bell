package com.ymatou.op.yms.bell.authentication

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

/**
 * Created by xuemingli on 16/9/9.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class SecurityConfig: WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var ldapAuthenticationProvider: LDAPAuthenticationProvider

    override fun configure(http: HttpSecurity?) {
        if (http == null) {
            return
        }
        http.addFilterBefore(LDAPAuthenticationFilter(), BasicAuthenticationFilter::class.java)
        http.authenticationProvider(ldapAuthenticationProvider)
        http.authorizeRequests()
                .antMatchers("/v1/authentication/login", "/ui/**", "/", "/static/**", "/favicon.ico")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable()
    }
}