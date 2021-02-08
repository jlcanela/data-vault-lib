libraryDependencies ++= Seq(
  "dev.zio" %% "zio-nio" % "1.0.0-RC10",
  "dev.zio" %% "zio" % "1.0.4",
  "dev.zio" %% "zio-macros" % "1.0.4",
  "org.rogach" %% "scallop" % "4.0.2",
  "org.apache.spark" %% "spark-core" % "3.1.0",
  "org.apache.spark" %% "spark-sql" % "3.1.0",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.8.8",
  "info.picocli" % "picocli" % "4.2.0",
  "org.json4s" % "json4s-native_2.12" % "3.7.0-M8",
  "com.github.pureconfig" %% "pureconfig" % "0.14.0",
  "com.github.pureconfig" %% "pureconfig-yaml" % "0.14.0",
  "com.github.scopt" %% "scopt" % "4.0.0",
  "com.tngtech.jgiven" % "jgiven-junit" % "0.15.3" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.vladsch.flexmark" % "flexmark-all" % "0.35.10" % "test",
  "org.scalatest" %% "scalatest" % "3.2.3" % "test",
  "org.scalatest" %% "scalatest-featurespec" % "3.2.3" % "test",
  "org.pegdown" % "pegdown" % "1.6.0" % "test",
  "io.cucumber" % "cucumber-junit" % "6.8.1" % "test",
  "io.cucumber" %% "cucumber-scala" % "6.8.1" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test" exclude ("junit", "junit")
)

scalaVersion := "2.12.10"

resolvers += Resolver.sonatypeRepo("releases")
autoCompilerPlugins := true
addCompilerPlugin(("org.scalamacros" %% "paradise" % "2.1.1") cross CrossVersion.full)


logBuffered in Test := false
coverageEnabled := true
coverageMinimum := 95
coverageFailOnMinimum := false

testOptions in Test ++= Seq(
  Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports"),
  Tests.Argument(TestFrameworks.ScalaTest, "-o")
)
