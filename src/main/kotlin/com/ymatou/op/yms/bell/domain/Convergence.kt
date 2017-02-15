package com.ymatou.op.yms.bell.domain

import com.avaje.ebean.Model
import java.sql.Timestamp
import javax.persistence.*

/**
 * Created by xuemingli on 2016/10/25.
 */
@Entity
@Table(name = "convergence")
class Convergence(
        @Id
        var id: Long? = null,
        @Column(length = 40, nullable = false, unique = true, columnDefinition = "CHAR(40)")
        var hash: String,
        @Column(nullable = false)
        var timestamp: Long,
        @Column(nullable = false)
        var count: Long
): Model() {
}