libraryDependencies ++= Seq(
  "dev.zio" %% "zio-nio-core" % "1.0.0-RC10",
  "dev.zio" %% "zio" % "1.0.4",
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

logBuffered in Test := false
coverageEnabled := true
coverageMinimum := 95
coverageFailOnMinimum := false

testOptions in Test ++= Seq(
  Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports"),
  Tests.Argument(TestFrameworks.ScalaTest, "-o")
)
