package com.ymatou.op.yms.bell.service

import com.avaje.ebean.EbeanServer
import com.ymatou.op.yms.bell.domain.*
import com.ymatou.op.yms.bell.mq.AlertMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Created by xuemingli on 2016/10/25.
 */
@Service
class HistoryService {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val md = MessageDigest.getInstance("SHA-1")

    @Autowired
    private lateinit var server: EbeanServer

    fun create(message: AlertMessage, recipient: String, channel: Channel, status: MessageStatus, item: Item) {
        val history = History(channel = channel, recipient = recipient,
                message = message.message, timestamp = message.timestamp,
                status = status, item = item)
        server.save(history)
    }



    fun getWaitingList(): List<History> {
        return server.find(History::class.java)
                .where()
                .eq("status", MessageStatus.WAITING)
                .findList()
    }

    fun getCount(item: Item, timestamp: Long): Map<String, Long>{
        val start = timestamp - TimeUnit.MINUTES.toMillis(item.convergence.toLong())
        val sql = "select count(item_id) as count, recipient from histories " +
                "where item_id = :item and timestamp >= :start and status = :status " +
                "group by recipient ;"

        val rs = server.createSqlQuery(sql)
                .setParameter("item", item.id)
                .setParameter("start", start)
                .setParameter("status", MessageStatus.QUEUED)
                .findList()
        return rs.map { it.getString("recipient") to it.getLong("count") }.toMap()
    }

    fun getQueue(item: Item, timestamp: Long, recipient: String): List<History> {
        val start = timestamp - TimeUnit.MINUTES.toMillis(item.convergence.toLong())
        return server.find(History::class.java).where()
                .eq("item", item)
                .and()
                .eq("recipient", recipient)
                .and()
                .eq("status", MessageStatus.QUEUED)
                .and()
                .ge("timestamp", start)
                .findList()
    }

    fun complete(histories: List<History>) {
        histories.forEach {
            it.status = MessageStatus.SENT
            server.update(it)
        }
    }

    fun failure(histories: List<History>) {
        histories.forEach {
            it.status = MessageStatus.FAILED
            server.update(it)
        }
    }
}