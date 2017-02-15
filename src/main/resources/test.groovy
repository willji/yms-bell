import java.util.concurrent.TimeUnit

def current = db
        .select("biz.order_sta")
        .field(fn.movingAverage(fn.sum("value"), 10))
        .interval(1)
        .period(30)
        .offset(3, TimeUnit.MINUTES)
        .groupBy('source')
        .query("yms")
def y = db
        .select("biz.order_sta")
        .field(fn.movingAverage(fn.sum("value"), 10))
        .interval(1)
        .period(30)
        .offset(1)
        .groupBy('source')
        .query("yms")
def data = current.mapping(moving_average: 't').join(y.mapping(moving_average: 'y'))

data.lambda {
    it.each {
        //println(it.key)
        def timestamp = it.key.timestamp
        it.value.each {
            try {
                //println("\t${it.t} => ${it.y}")
                alertor.info("biz.order_sta", "${it.t} => ${it.y}", timestamp)
            } catch (Exception e) {
                //logger.info(e.message)
                alertor.critical('exception', e.message, timestamp)
            }

        }
    }
}

System.currentTimeMillis()