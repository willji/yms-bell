package com.ymatou.op.yms.bell.service

import com.avaje.ebean.EbeanServer
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import com.ymatou.op.yms.bell.domain.Api
import com.ymatou.op.yms.bell.domain.App
import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.toHexString
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by xuemingli on 2016/10/17.
 */

@Service
class AppService {
    private val logger = LoggerFactory.getLogger(javaClass)!!
    private val md = MessageDigest.getInstance("SHA-1")
    private val fmt = DateTimeFormatter.ofPattern("yyMMdd")

    private val cache = CacheBuilder.newBuilder()
            .maximumSize(100000)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(CacheLoader.from { id: String? -> load(id) })

    private val clientRef = AtomicReference<MongoClient>()

    @Autowired
    private lateinit var server: EbeanServer

    @Value("\${mongodb.performance.host}")
    private lateinit var host: String

    @Value("\${mongodb.performance.port}")
    private var port: Int = 30001

    @Value("\${mongodb.performance.db}")
    private lateinit var db: String

    @Value("\${mongodb.performance.col}")
    private lateinit var col: String

    @Value("\${mongodb.performance.min.count}")
    private var minCount = 1000

    private fun getClient(): MongoClient {
        var client = clientRef.get()
        if (client == null) {
            client = MongoClient(ServerAddress(host, port))
            clientRef.set(client)
        }
        return client
    }

    private fun load(id: String?): Int {
        val yesterday = LocalDate.now().minusDays(1)
        val client = getClient()
        val database = client.getDatabase(db)
        val collection = database.getCollection("perfdata${yesterday.format(fmt)}")
        val query = BasicDBObject()
        query.put("IndexId", id)
        return collection.find(query).map { it.get("Value", Integer::class.java).toInt() }.sum()
    }

    fun save(name: String): App {
        var app = server.find(App::class.java).where().eq("name", name).findUnique()
        if (app == null) {
            app = App(name = name)
            server.save(app)
        }
        return app
    }

    fun saveApi(name: String, app: App): Api {
        val hash = md.digest(name.toByteArray()).toHexString()
        var api = server.find(Api::class.java).where().eq("hash", hash).and().eq("app", app).findUnique()
        if (api == null) {
            api = Api(name = name, app = app, hash = hash)
            server.save(api)
        }
        return api
    }

    fun sync() {
        val apps = mutableMapOf<String, App>()
        val client = getClient()
        val database = client.getDatabase(db)
        val collection = database.getCollection(col)
        val cursor = collection.find()
        cursor.forEach {
            val appName = it.get("AppId", String::class.java)
            val apiName = it.get("Counter", String::class.java)
            val id = it.get("_id", String::class.java)
            try {
                val app = apps.getOrPut(appName, { save(appName) })
                var insert = true
                app.blackList.forEach {
                    if (it.content.toRegex().matches(apiName)) {
                        deleteApi(app, apiName)
                        insert = false
                    }
                }
                if (insert) {
                    val count = cache[id]
                    if (count > minCount) {
                        saveApi(apiName, app)
                    }
                }
            } catch (e: Exception) {
                logger.error("sync app $appName api $apiName error", e)
            }
        }
    }

    fun get(name: String): App? {
        return server.find(App::class.java)
                .where()
                .eq("name", name)
                .findUnique()
    }

    fun get(id: Long): App? {
        return server.find(App::class.java, id)
    }

    fun search(q: String, item: Long): List<String> {
        val sql = "select distinct name from apps " +
                "left join items_apps on items_apps.apps_id = apps.id " +
                "where (items_id is NULL or items_id != :item) and name like :kw order by apps.name"
        return server.createSqlQuery(sql)
                .setParameter("item", item)
                .setParameter("kw", "%$q%")
                .setMaxRows(20).findList().map { it.getString("name") }
    }

    fun searchApi(q: String, name: String, item: Long): List<String> {
        val app = get(name) ?: return emptyList()
        val sql = "select distinct name from apis " +
                "left join items_apis on items_apis.apis_id = apis.id " +
                "where (items_id is NULL or items_id != :item) and app=:app and name like :kw order by apis.name"
        return server.createSqlQuery(sql)
                .setParameter("item", item)
                .setParameter("app", app.id)
                .setParameter("kw", "%$q%")
                .setMaxRows(20).findList().map { it.getString("name") }

    }

    fun getApi(appName: String, apiName: String): Api? {
        val app = get(appName)
        return server.find(Api::class.java).where()
                .eq("hash", md.digest(apiName.toByteArray()).toHexString())
                .and()
                .eq("app", app).findUnique()
    }

    fun getApiSetByAppAndItems(app: App, item: Item): List<Api> {
        return server.find(Api::class.java)
                .where()
                .eq("app", app)
                .and()
                .eq("items.id", item.id)
                .findList()
    }

    fun deleteApi(app: App, hash: String) {
        server.find(Api::class.java).where().eq("hash", hash).and().eq("app", app).delete()
    }

    fun itemHasOtherApi(api: Api, item: Item): Boolean{
        return server.find(Api::class.java).where()
                .eq("items.id", item.id)
                .and()
                .eq("app", api.app)
                .and()
                .ne("id", api.id).findCount() <= 0
    }

}