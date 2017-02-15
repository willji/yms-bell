package com.ymatou.op.yms.bell.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.squareup.okhttp.*
import com.ymatou.op.yms.bell.domain.Channel
import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.domain.MessageStatus
import com.ymatou.op.yms.bell.mq.AlertLevel
import com.ymatou.op.yms.bell.mq.AlertMessage
import com.ymatou.op.yms.bell.mq.BatchAlertMessage
import freemarker.template.Configuration
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Point
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

/**
 * Created by xuemingli on 16/9/7.
 */

@Service
class SendService {
    private val logger = LoggerFactory.getLogger(javaClass)!!
    private val configuration = Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)
    private val client = OkHttpClient()
    private val mapper: ObjectMapper = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule()).build()

    init {
        configuration.setClassForTemplateLoading(javaClass, "/templates/")
        client.setConnectTimeout(2, TimeUnit.SECONDS)
        client.setReadTimeout(2, TimeUnit.SECONDS)
        client.setWriteTimeout(2, TimeUnit.SECONDS)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    private lateinit var db: InfluxDB


    @Autowired
    lateinit var mailSender: JavaMailSender

    @Autowired
    lateinit var itemService: ItemService

    @Autowired
    lateinit var cmdbService: CmdbService

    @Autowired
    private lateinit var historyService: HistoryService

    @Value("\${spring.mail.username}")
    lateinit var from: String

    @Value("\${cmdb.url}")
    lateinit var url: String

    @Value("\${cmdb.username}")
    lateinit var username: String

    @Value("\${cmdb.password}")
    lateinit var password: String

    @Value("\${sms.url}")
    lateinit var smsUrl: String

    @Value("\${use.cmdb}")
    var useCmdb: Boolean = false

    @Value("\${influxdb.write.url}")
    lateinit var influxdbUrl: String

    @Value("\${influxdb.write.user}")
    lateinit var influxdbUser: String

    @Value("\${influxdb.write.password}")
    lateinit var influxdbPassword: String

    @Value("\${alert.default.sms}")
    lateinit var defaultSms: String

    @Value("\${alert.default.mail}")
    lateinit var defaultMail: String

    @Value("\${alert.send.sms}")
    var sendSms: Boolean = true

    @Value("\${alert.send.mail}")
    var sendMail: Boolean = true




    @PostConstruct
    fun connect() {
        db = InfluxDBFactory.connect(influxdbUrl, influxdbUser, influxdbPassword)
        db.enableBatch(2000, 1000, TimeUnit.MILLISECONDS)
    }

    private fun getRecipients(message: AlertMessage): Pair<Set<String>, Set<String>> {
        val mailSet = mutableSetOf<String>()
        val smsSet = mutableSetOf<String>()
        val item = itemService.get(message.item)
        if (item == null) {
            logger.error("get item for message ${message.item} ${message.message}")
            return mailSet to smsSet
        }
        val ret = mutableListOf<Pair<String?, String?>>()
        ret.addAll(item.subscribers.map { it.email to it.mobile })
        if (useCmdb) {
            if (message.additional.containsKey("app")) {
                ret.addAll(cmdbService.getRecipientsFromApp(message.additional["app"]!!))
            }
            if (message.additional.containsKey("host")) {
                ret.addAll(cmdbService.getRecipientsFromHost(message.additional["host"]!!))
            }
        }
        ret.forEach {
            if (it.first != null) {
                mailSet.add(it.first!!)
            }
            if (it.second != null) {
                smsSet.add(it.second!!)
            }
        }
        defaultMail.split(",").map(String::trim).forEach { mailSet.add(it) }
        defaultSms.split(",").map(String::trim).forEach { smsSet.add(it) }

        return mailSet to smsSet
    }

    private fun persist(message: AlertMessage) {
        var apps = listOf<String>()
        if (!message.additional.containsKey("app") && message.additional.containsKey("host")) {
            try {
                apps = cmdbService.getAppFromHost(message.additional["host"]!!)
            } catch (e: Exception) {
                logger.error("get app via host fail", e)
            }
        }
        if (message.additional.containsKey("app")) {
            apps = listOf(message.additional["app"]!!)
        }

        val builder = Point.measurement("yms.event")
                .time(message.timestamp, TimeUnit.MILLISECONDS)
                .addField("value", message.level.ordinal.toFloat())
                .addField("name", message.level.name)
                .addField("message", message.message)
                .tag("item", message.item.toString())
        if (apps.size > 0) {
            builder.tag("app", apps.sorted().joinToString(","))
        }
        if (message.additional.containsKey("host")) {
            builder.tag("host", message.additional["host"])
        }
        db.write("yms", "default", builder.build())

    }

    fun send(message: AlertMessage) {
        persist(message)
        val item = itemService.get(message.item) ?: return
        val (mailSet, smsSet) = getRecipients(message)
        if (message.level.ordinal > AlertLevel.INFO.ordinal) {
            if (message.level.ordinal > AlertLevel.WARNING.ordinal) {
                send_sms(message, smsSet, item)
                send_mail(message, mailSet, item)
                return
            }
            if (item.convergence <= 0) {
                send_mail(message, mailSet, item)
            } else {
                queue(message, mailSet, item)
            }
        }
    }

    fun queue(message: AlertMessage, recipients: Set<String>, item: Item) {
        recipients.forEach {
            historyService.create(message, it, Channel.MAIL, MessageStatus.QUEUED, item)
        }
    }

    fun send_mail(message: AlertMessage, recipients: Set<String>, item: Item) {
        if (!sendMail) {
            recipients.forEach {
                historyService.create(message, it, Channel.MAIL, MessageStatus.NO_ACTION, item)
            }
            return
        }
        val msg = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(msg, true)
        val dt = Instant.ofEpochMilli(message.timestamp).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        helper.setFrom(from)
        helper.setSubject("【YMS】【${message.level}】【$dt】${message.message}")
        val html = FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("mail.ftl"), message)
        helper.setText(html, true)
        recipients.forEach {
            if (!it.contains('@')) {
                helper.setTo("$it@ymatou.com")
            } else {
                helper.setTo(it)
            }
            try {
                mailSender.send(msg)
                historyService.create(message, it, Channel.MAIL, MessageStatus.SENT, item)
            } catch (e: Exception) {
                logger.error("send message ${message.id} to $it@ymatou.com error", e)
                historyService.create(message, it, Channel.MAIL, MessageStatus.WAITING, item)
            }
        }
    }

    fun send_sms(message: AlertMessage, recipients: Set<String>, item: Item) {
        if (!sendSms) {
            recipients.forEach {
                historyService.create(message, it, Channel.SMS, MessageStatus.NO_ACTION, item)
            }
            return
        }
        val dt = Instant.ofEpochMilli(message.timestamp).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val msg = "【${message.level}】【$dt】${message.message}"
        recipients.forEach {
            val uri = HttpUrl.parse(smsUrl)
                    .newBuilder()
                    .addQueryParameter("content", msg)
                    .addQueryParameter("appId", "sendplatform.ops.ymatou.cn")
                    .addQueryParameter("phone", it)
                    .build()
            val request = Request.Builder()
                    .url(uri)
                    .post(RequestBody.create(null, ByteArray(0)))
                    .build()
            try {
                client.newCall(request).execute()
                historyService.create(message, it, Channel.SMS, MessageStatus.SENT, item)
            } catch (e: Exception) {
                logger.error("send message ${message.id} to $it error", e.message)
                historyService.create(message, it, Channel.SMS, MessageStatus.WAITING, item)
            }
        }
    }

    fun batchSend(message: BatchAlertMessage) {
        val item = itemService.get(message.item) ?: return
        val histories = historyService.getQueue(item, message.timestamp, message.recipient)
        val msg = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(msg, true)
        val dt = Instant.ofEpochMilli(message.timestamp).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        logger.info(histories.size.toString())
        helper.setFrom(from)
        helper.setSubject("【YMS】【$dt】【${message.count}】${item.name}")
        val html = FreeMarkerTemplateUtils
                .processTemplateIntoString(configuration.getTemplate("batch_mail.ftl"), mapOf("histories" to histories))
        helper.setText(html, true)
        if (!message.recipient.contains('@')) {
            helper.setTo("${message.recipient}@ymatou.com")
        } else {
            helper.setTo(message.recipient)
        }
        //helper.setTo("${message.recipient}@ymatou.com")
        try {
            mailSender.send(msg)
            historyService.complete(histories)
        } catch (e: Exception) {
            historyService.failure(histories)
            logger.error("send batch message ${item.id} to ${message.recipient}@ymatou.com error", e)
        }
    }
}