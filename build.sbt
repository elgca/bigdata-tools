organization := "kervin"

name := "bigdata-tools"

version := "0.1-SNAPSHOT"
isSnapshot := true
scalaVersion := "2.11.8"

val sparkVersion = "2.3.1"
val hbaseVersion = "1.2.1"
// https://mvnrepository.com/artifact/org.apache.spark/spark-core
// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided"
//libraryDependencies += "org.apache.spark" %% "spark-core"       % sparkVersion % "provided"
libraryDependencies += "org.apache.hbase" % "hbase-common" % hbaseVersion % "provided"
libraryDependencies += "org.apache.hbase" % "hbase-client" % hbaseVersion % "provided"
// https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.6.0-M4"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.7"
// https://mvnrepository.com/artifact/com.101tec/zkclient
//libraryDependencies += "com.101tec" % "zkclient" % "0.10"
libraryDependencies += "com.github.adyliu" % "zkclient" % "2.1.1"
// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.1"
libraryDependencies += "org.sorm-framework" % "sorm" % "0.3.21"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.9.1"
resolvers += "micronautics/scala on bintray" at "http://dl.bintray.com/micronautics/scala"
libraryDependencies += "com.micronautics" %% "html-email" % "0.1.2" withSources()

libraryDependencies += "org.apache.kafka" % "kafka-clients" % "1.1.0"
// https://mvnrepository.com/artifact/org.markdownj/markdownj-core
libraryDependencies += "org.markdownj" % "markdownj-core" % "0.4"
//libraryDependencies += "org.apache.kafka" %% "kafka" % "1.1.0"
//libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.9.0.0"
//libraryDependencies += "org.apache.kafka" %% "kafka" % "0.9.0.0"