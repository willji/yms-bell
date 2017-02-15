package com.ymatou.op.yms.bell.domain

import com.avaje.ebean.Model
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

/**
 * Created by xuemingli on 2016/10/24.
 */
@Entity
@Table(name = "blacklist")
@JsonIgnoreProperties("app")
class BlackList(
        @Id
        var id: Long? = null,
        @Column(nullable = false, columnDefinition = "TEXT")
        var content: String,
        @ManyToOne(fetch = javax.persistence.FetchType.LAZY)
        @JoinColumn(nullable = false)
        var app: App
): Model() {

}