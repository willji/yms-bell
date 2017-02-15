package com.ymatou.op.yms.bell.domain

import com.avaje.ebean.Model
import javax.persistence.*

/**
 * Created by xuemingli on 16/9/9.
 */
@Entity
@Table(name = "roles")
class Role(
        @Id
        @GeneratedValue
        var id: Long? = null,
        @Column(length = 128, nullable = false, unique = true)
        var name: String,
        @Column(columnDefinition = "TEXT")
        var descriptor: String
): Model() {


}