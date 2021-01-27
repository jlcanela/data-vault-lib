libraryDependencies ++= Seq(
   "info.picocli" % "picocli" % "4.2.0",
   "org.json4s" % "json4s-native_2.12" % "3.7.0-M8",
  "com.tngtech.jgiven" % "jgiven-junit" % "0.15.3" % "test",
  "junit" % "junit" % "4.12" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.pegdown" % "pegdown" % "1.6.0" % "test",
   "io.cucumber" % "cucumber-junit" % "6.8.1" % "test",
   "io.cucumber" %% "cucumber-scala" % "6.8.1" % "test",
   "com.novocode" % "junit-interface" % "0.11" % "test" exclude("junit", "junit")
)


testOptions in Test ++= Seq(
   Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports"),
   Tests.Argument(TestFrameworks.ScalaTest, "-o"),
   Tests.Argument(TestFrameworks.ScalaTest, "-o")
)