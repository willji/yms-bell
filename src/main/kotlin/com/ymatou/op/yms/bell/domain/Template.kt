package com.ymatou.op.yms.bell.domain

import com.avaje.ebean.Model
import javax.persistence.*

/**
 * Created by xuemingli on 2016/10/13.
 */
@Entity
@Table(name = "template")
class Template(
        @Id
        var id: Long? = null,
        @Column(name = "name", unique = true, nullable = false, length = 128)
        var name: String,
        @Column(name ="script", nullable = false, columnDefinition = "LONGTEXT")
        var script: String,
        @ManyToOne
        @JoinColumn(name = "creator")
        var creator: User? = null,
        @Column(nullable = false)
        var timestamp: Long = System.currentTimeMillis(),
        @OneToMany
        @JoinColumn(name = "template")
        var items: Set<Item> = setOf()
):Model() {

}