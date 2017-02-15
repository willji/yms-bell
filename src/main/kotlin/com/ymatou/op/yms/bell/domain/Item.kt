package com.ymatou.op.yms.bell.domain

import com.avaje.ebean.Model
import com.avaje.ebean.annotation.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.ymatou.op.yms.bell.template.Variable
import com.ymatou.op.yms.bell.template.Variables
import java.sql.Timestamp
import javax.persistence.*

/**
 * Created by xuemingli on 16/9/8.
 */
@Entity
@Table(name = "items")
@JsonIgnoreProperties("template")
class Item(
        //meta
        @Id
        var id: Long? = null,
        @Column(length = 128, nullable = false, unique = true)
        var name: String,
        @Column(nullable = false)
        var enable: Boolean = true,
        @Column(length = 128, nullable = false)
        var cron: String,
        @Column(nullable = true, columnDefinition = "TEXT")
        var descriptor: String? = null,
        @Column(nullable = false)
        var convergence: Int = 1,

        //template
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "template", nullable = false)
        var template: Template? = null,
        @Column(nullable = false, columnDefinition = "LONGTEXT")
        var variables: String,

        @Transient
        var templateId: Long = 0,
        @Transient
        var templateName: String = "",

        @Transient
        var vars: Map<String, Variable> = mapOf(),

        @Transient
        var available: Boolean = true,

        @ManyToOne
        @JoinColumn(name = "creator")
        var creator: User? = null,
        @Column(nullable = false, name = "create_time")
        @WhenCreated
        var createTime: Timestamp = Timestamp(System.currentTimeMillis()),

        @ManyToOne
        @WhoModified
        @JoinColumn(name = "modifier")
        var modifier: User? = null,
        @Column(nullable = false, name = "update_time")
        @WhenModified
        var lastModifiedTime: Timestamp = Timestamp(System.currentTimeMillis()),

        @ManyToMany
        @JoinTable(name = "subscribers")
        var subscribers: MutableSet<User> = mutableSetOf()
): Model() {
}