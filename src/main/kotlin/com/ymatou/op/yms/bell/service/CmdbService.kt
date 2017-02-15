package com.ymatou.op.yms.bell.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.squareup.okhttp.*
import com.ymatou.op.yms.bell.domain.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * Created by xuemingli on 2016/10/17.
 */
@Service
class CmdbService {
    private val client = OkHttpClient()
    private val mapper: ObjectMapper = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule()).build()

    init {
        client.setConnectTimeout(2, TimeUnit.SECONDS)
        client.setReadTimeout(2, TimeUnit.SECONDS)
        client.setWriteTimeout(2, TimeUnit.SECONDS)
    }

    @Value("\${cmdb.url}")
    lateinit var url: String

    @Value("\${cmdb.username}")
    lateinit var username: String

    @Value("\${cmdb.password}")
    lateinit var password: String

    @Value("\${alert.send.dev:true}")
    var sendToDev: Boolean = true

    private fun getToken(): String {
        val request = Request.Builder()
                .url("$url/api/cmdb/token/")
                .post(RequestBody.create(MediaType.parse("application/json"), mapper.writeValueAsBytes(mapOf("username" to username, "password" to password))))
                .build()
        val res = client.newCall(request).execute()
        if (res.isSuccessful) {
            res.body().use {
                val node = mapper.readTree(it.bytes())
                return node["token"].asText()
            }
        }
        throw RuntimeException("get token fail")
    }

    fun getAllApps(): List<String> {
        val token = getToken()
        val builder = HttpUrl.parse("$url/api/cmdb/applications/application.json").newBuilder()
        var result = listOf<String>()
        val size = 100
        var page = 1
        while (true) {
            val uri = builder.addQueryParameter("page", page.toString()).addQueryParameter("page_size", size.toString()).build()
            val request = Request.Builder().url(uri)
                    .header("Authorization", "token $token")
                    .get().build()
            val res = client.newCall(request).execute()
            if (res.isSuccessful) {
                res.body().use {
                    val node = mapper.readTree(it.bytes())
                    result += node["results"].elements().asSequence().map { it.get("name").asText() }
                    if (node.get("next").isNull) {
                        return result
                    }
                }
            }
            page += 1
        }
    }

    fun getAllUsers(): List<User> {
        val token = getToken()
        val builder = HttpUrl.parse("$url/api/cmdb/contacts/contact.json").newBuilder()
        var result = listOf<User>()
        val size = 100
        var page = 1
        while (true) {
            val uri = builder.addQueryParameter("page", page.toString()).addQueryParameter("page_size", size.toString()).build()
            val request = Request.Builder().url(uri)
                    .header("Authorization", "token $token")
                    .get().build()
            val res = client.newCall(request).execute()
            if (res.isSuccessful) {
                res.body().use {
                    val node = mapper.readTree(it.bytes())
                    result += node["results"].elements().asSequence().map {
                        User(name = it.get("chinese_name").asText(),
                                email = it.get("email").asText().split('@')[0],
                                mobile = it.get("mobile").asText(), isNew = false)
                    }
                    if (node.get("next").isNull) {
                        return result
                    }
                }
            }
            page += 1
        }
    }

    fun getUserInfo(name: String): List<Pair<String?, String?>> {
        val token = getToken()
        val uri = HttpUrl.parse("$url/api/cmdb/contacts/contact.json")
                .newBuilder().addQueryParameter("search", name).build()
        val request = Request.Builder()
                .url(uri)
                .header("Authorization", "token $token")
                .get().build()
        val res = client.newCall(request).execute()
        if (res.isSuccessful) {
            res.body().use {
                val node = mapper.readTree(it.bytes())
                return node["results"].elements().asSequence().map {
                    it["email"].asText() to it["mobile"].asText()
                }.toList()
            }
        }
        throw RuntimeException("get user info error")
    }

    fun getRecipientsFromApp(app: String): List<Pair<String?, String?>> {
        val token = getToken()
        val uri = HttpUrl.parse("$url/api/cmdb/applications/application.json")
                .newBuilder().addQueryParameter("name", app).build()
        val request = Request.Builder()
                .url(uri)
                .header("Authorization", "token $token")
                .get().build()
        val res = client.newCall(request).execute()
        if (res.isSuccessful) {
            res.body().use {
                val node = mapper.readTree(it.bytes())
                if (sendToDev) {
                    return node["results"].elements().asSequence().toList().map {
                        listOf(getUserInfo(it["owner"].asText()),
                                getUserInfo(it["backup_owner"].asText()),
                                getUserInfo(it["ops_owner"].asText()))
                    }.flatten().flatten()
                }
                return node["results"].elements().asSequence().toList().map {
                    getUserInfo(it["ops_owner"].asText())
                }.flatten()
            }
        }
        throw RuntimeException("get user info error")
    }

    fun getRecipientsFromHost(host: String): List<Pair<String?, String?>> {
        val token = getToken()
        val uri = HttpUrl.parse("$url/api/cmdb/applications/applicationgroup.json")
                .newBuilder().addQueryParameter("ipaddresses", host).build()
        val request = Request.Builder()
                .url(uri)
                .header("Authorization", "token $token")
                .get().build()
        val res = client.newCall(request).execute()
        if (res.isSuccessful) {
            res.body().use {
                val node = mapper.readTree(it.bytes())
                return node["results"].elements().asSequence().toList().map {
                    listOf(getUserInfo(it["owner"].asText()), getUserInfo(it["backup_owner"].asText()), getUserInfo(it["ops_owner"].asText()))
                }.flatten().flatten()
            }
        }
        throw RuntimeException("get user info error")
    }

    fun getAppFromHost(host: String): List<String> {
        val token = getToken()
        val uri = HttpUrl.parse("$url/api/cmdb/applications/applicationgroup.json")
                .newBuilder().addQueryParameter("ipaddresses", host).build()
        val request = Request.Builder()
                .url(uri)
                .header("Authorization", "token $token")
                .get().build()
        val res = client.newCall(request).execute()
        if (res.isSuccessful) {
            res.body().use {
                val node = mapper.readTree(it.bytes())
                return node["results"].elements().asSequence().toList().map {
                    it["application"].asText()
                }
            }
        }
        throw RuntimeException("get user info error")
    }
}