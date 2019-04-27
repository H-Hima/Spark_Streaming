package ir.sharif.de2019_spark_streaming

import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.{Minutes, Seconds, State, StateSpec}

import scala.collection.mutable


object App  {
  def main(args: Array[String]): Unit = {
    println("Salaam")

    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)

    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    val (spark, ssc) = SparkStreamFactory.createSparkStream("DE_Spark_Streaming", 10)

    ssc.checkpoint("/tmp/")
//    import spark.implicits._

    val textData1 = ssc.sparkContext.textFile("/tmp/samples/1.txt")
    val textData2 = ssc.sparkContext.textFile("/tmp/samples/2.txt")
    //val stream = ssc.queueStream(mutable.Queue(textData1, textData2))

    val stream = ssc.socketTextStream("127.0.0.1", 8008)

    stream.print()

    val squareStream = stream.map(record => {
      "The Square of \"" + record + "\" is " + {
        try {
          (record.toLong * record.toLong).toString
        }
        catch {
          case ex:Throwable => "NAN"
        }
      }
    })
    squareStream.foreachRDD(rdd => {
      println("Output result of batch:")
      rdd.foreach(println)
    })

    val stateSpec = StateSpec.function(updateState _)
      .numPartitions(10)
      .timeout(Minutes(1))

    val stateStream = stream.map(record => (record, 1)).mapWithState(stateSpec)
    stateStream.print()

    val stateSnapshot = stateStream.stateSnapshots()
    stateSnapshot.foreachRDD(rdd => {
      rdd.collect().foreach(println)
    })

    SparkStreamFactory.startStream(ssc)
  }

  def updateState(key: String, value: Option[Int], state: State[Int]): (String, Int, Int) = {
    val prevValue = state.getOption().getOrElse(0)

    if(state.isTimingOut()) {
      (key, prevValue, -1)
    }
    else {
      val nextValue = prevValue + value.getOrElse(0)

      if(nextValue > 10)
        state.remove
      else
        state.update(nextValue)

      (key, prevValue, nextValue)
    }
  }
}
