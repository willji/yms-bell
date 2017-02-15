package com.ymatou.op.yms.bell.domain

import com.avaje.ebean.Model
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

/**
 * Created by xuemingli on 2016/10/24.
 */
@Entity
@Table(name = "apis", uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("hash", "app"))))
@JsonIgnoreProperties("app")
class Api(
        @Id
        var id: Long? = null,
        @Column(nullable = false, columnDefinition = "TEXT")
        var name: String,
        @Column(length = 40, nullable = false, columnDefinition = "CHAR(40)")
        var hash: String,
        @ManyToOne(fetch = javax.persistence.FetchType.LAZY)
        @JoinColumn(name = "app", nullable = false)
        var app: App,
        @ManyToMany
        @JoinTable(name = "items_apis")
        var items: MutableSet<Item> = mutableSetOf()
): Model() {

}