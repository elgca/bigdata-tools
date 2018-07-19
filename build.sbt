name := "bigdata-tools"

version := "0.1"

scalaVersion := "2.11.8"

val sparkVersion = "2.3.1"
val hbaseVersion = "1.2.1"
// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.hbase" % "hbase-common" % hbaseVersion % "provided"
// https://mvnrepository.com/artifact/com.101tec/zkclient
//libraryDependencies += "com.101tec" % "zkclient" % "0.10"
libraryDependencies += "com.github.adyliu" % "zkclient" % "2.1.1"
// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.1"
libraryDependencies += "org.sorm-framework" % "sorm" % "0.3.21"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.9.1"
