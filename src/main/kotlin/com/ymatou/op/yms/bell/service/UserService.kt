package com.ymatou.op.yms.bell.service

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.PagedList
import com.ymatou.op.yms.bell.authentication.SecurityService
import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by xuemingli on 16/9/9.
 */
@Service
class UserService {
    @Autowired
    lateinit var server: EbeanServer

    @Autowired
    lateinit var securityService: SecurityService

    fun create(user: User): User {
        server.save(user)
        return user
    }

    fun createOrUpdate(user: User): User {
        return server.find(User::class.java).where().eq("email", user.email).findUnique() ?: create(user)
    }

    fun getByEmail(email: String): User? {
        return server.find(User::class.java).where().eq("email", email).findUnique()
    }

    fun getMySubscriptions(page: Int, size: Int): PagedList<Item> {
        return server.find(Item::class.java).where()
                .eq("subscribers.id", securityService.getPrincipal().id)
                .setFirstRow(page-1 * size)
                .setMaxRows(size)
                .findPagedList()
    }
}