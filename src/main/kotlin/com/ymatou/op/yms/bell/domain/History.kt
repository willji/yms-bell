package com.ymatou.op.yms.bell.domain

import com.avaje.ebean.Model
import com.avaje.ebean.annotation.Index
import javax.persistence.*

/**
 * Created by xuemingli on 2016/10/26.
 */
@Entity
@Table(name = "histories")
class History(
        @Id
        var id: Long? = null,
        @Column(name = "channel", nullable = false)
        @Index
        var channel: Channel = Channel.MAIL,
        @Column(name = "recipient", nullable = false, length = 128)
        @Index
        var recipient: String? = null,
        @Column(name = "message", nullable = false)
        @Lob
        var message: String? = null,
        @Column(name = "timestamp", nullable = false)
        @Index
        var timestamp: Long = System.currentTimeMillis(),
        @Column(name = "status", nullable = false)
        @Index
        var status: MessageStatus = MessageStatus.WAITING,
        @ManyToOne
        var item: Item
): Model() {
}