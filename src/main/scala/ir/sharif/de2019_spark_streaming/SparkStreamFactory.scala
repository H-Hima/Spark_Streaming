package ir.sharif.de2019_spark_streaming

import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkStreamFactory extends Serializable {

  def createSparkStream(appName: String, duration: Long): (SparkSession, StreamingContext) = {

    val sparkConf = new SparkConf()
//      .set("spark.master",Configs.get("spark.master"))
//      .set("spark.streaming.backpressure.enabled",Configs.get("spark.streaming.backpressure.enabled"))
//      .set("spark.streaming.backpressure.initialRate",Configs.get("spark.streaming.backpressure.initialRate"))
//      .set("spark.streaming.kafka.maxRatePerPartition",Configs.get("spark.streaming.kafka.maxRatePerPartition"))
//      .set("spark.streaming.kafka.consumer.cache.enabled",Configs.get("spark.streaming.kafka.consumer.cache.enabled"))
//      .set("spark.serializer",Configs.get("spark.serializer"))
//      .set("spark.driver.extraClassPath",Configs.get("spark.driver.extraClassPath"))
//      .set("spark.driver.maxResultSize",Configs.get("spark.driver.maxResultSize"))
//      .set("spark.driver.memory","500m")
//      .set("spark.driver.memoryOverhead",Configs.get("spark.driver.memoryOverhead"))
//      .set("spark.executor.memory","500m")
//      .set("spark.executor.memoryOverhead","100m")
      .set("spark.executor.cores","10")
//      .set("spark.cores.max","1")
//      .set("spark.executor.extraJavaOptions",Configs.get("spark.executor.extraJavaOptions"))
      .set("spark.executor.instances","10")

    val spark = SparkSession
      .builder()
      .config(sparkConf).appName(appName)
      .enableHiveSupport()
      .getOrCreate()

    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(duration))

    (spark, ssc)
  }

  def startStream(ssc: StreamingContext): Unit = {
    ssc.start()
//    new Thread() {
//      override def run() {
//      }
//    }.start()
//    sys.ShutdownHookThread {
//      ssc.stop(stopSparkContext = true, stopGracefully = true)
//    }
    ssc.awaitTermination()
    ssc.stop()
  }
}
