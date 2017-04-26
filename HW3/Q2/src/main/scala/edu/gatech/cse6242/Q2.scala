package edu.gatech.cse6242

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._

object Q2 {

    case class Record(src: String, tgt: String, weight: Int)

    def main(args: Array[String]) {
        val sc = new SparkContext(new SparkConf().setAppName("Q2"))
        val sqlContext = new SQLContext(sc)
        import sqlContext.implicits._

        // read the file
        val file = sc.textFile("hdfs://localhost:8020/" + args(0))
        
        /* TODO: Needs to be implemented */
        //case class Record(src: String, tgt: String, weight: Int)

        val rec = file.map(_.split("\t")).map(x => Record(x(0), x(1), x(2).trim.toInt)).toDF()
        rec.registerTempTable("rec")

        rec.filter("weight != 1")

        val accout = rec.select("src", "weight").groupBy("src").agg(sum(rec("weight"))).withColumnRenamed("sum(weight)", "w_out")

        val accin = rec.select("tgt", "weight").groupBy("tgt").agg(sum(rec("weight"))).withColumnRenamed("sum(weight)", "w_in")

        val df = accout.join(accin, accout("src") === accin("tgt"))
                .selectExpr("src", "w_in - w_out")

        val file1 = df.map(f => f.mkString("\t"))

        // store output on given HDFS path.
        // YOU NEED TO CHANGE THIS
        file1.saveAsTextFile("hdfs://localhost:8020/" + args(1))
    }
}
