package com.ymatou.op.yms.bell.domain

import com.avaje.ebean.Model
import javax.persistence.*
import com.ymatou.op.yms.bell.domain.BlackList

/**
 * Created by xuemingli on 2016/10/13.
 */
@Entity
@Table(name = "apps")
class App(
        @Id
        var id: Long? = null,
        @Column(unique = true, length = 128, nullable = false)
        var name: String,
        @OneToMany(mappedBy = "app")
        var blackList: MutableSet<BlackList> = mutableSetOf(),
        @ManyToMany
        @JoinTable(name = "items_apps")
        var items: MutableSet<Item> = mutableSetOf()
): Model() {
}