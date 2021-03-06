import BuildHelper._

inThisBuild(
  List(
    organization := "dev.zio",
    homepage := Some(url("https://github.com/jlcanela/data-vault-lib/")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "jlcanela",
        "Jean-Luc CANELA",
        "na",
        url("https://github.com/jlcanela")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/jlcanela/data-vault-lib/"),
        "scm:git:git@github.com:jlcanela/data-vault-lib.git"
      )
    )
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

Global / onChangedBuildSource := ReloadOnSourceChanges
Global / testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

sonarUseExternalConfig := true

scapegoatVersion in ThisBuild := "1.3.9"
scapegoatReports := Seq("xml")

coverageMinimum := 85
coverageFailOnMinimum := true

libraryDependencies ++= Dependencies.datavault
scalaVersion := customScalaVersion
scalacOptions := customScalacOptions
