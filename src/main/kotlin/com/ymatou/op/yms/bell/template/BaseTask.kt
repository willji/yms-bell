package com.ymatou.op.yms.bell.template

import com.ymatou.op.yms.bell.domain.Item
import com.ymatou.op.yms.bell.query.Function
import com.ymatou.op.yms.bell.query.Operator
import com.ymatou.op.yms.bell.query.QueryNode
import com.ymatou.op.yms.bell.service.AlertService
import com.ymatou.op.yms.bell.service.ItemService
import org.influxdb.InfluxDB
import org.slf4j.LoggerFactory

/**
 * Created by xuemingli on 2016/10/13.
 */
abstract class BaseTask {
    protected val fn = Function
    protected val logger = LoggerFactory.getLogger(javaClass)!!

    protected val eq = Operator.EQ
    protected val ne = Operator.NE
    protected val lt = Operator.LT
    protected val le = Operator.LE
    protected val gt = Operator.GT
    protected val ge = Operator.GE
    protected val match = Operator.MATCH
    protected val not_match = Operator.NOT_MATCH

    protected val INT = VariableType.INT
    protected val FLOAT = VariableType.FLOAT
    protected val STRING = VariableType.STRING

    lateinit var db: QueryNode
    lateinit var alertor: AlertService
    lateinit var item: Item
    lateinit var vars: Variables
    lateinit var service: ItemService

    open fun setup() {

    }

    abstract fun run()

    open fun cleanup() {

    }
}