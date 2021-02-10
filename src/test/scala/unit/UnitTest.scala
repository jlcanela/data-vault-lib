package unit

import zio._
import zio.test.Assertion._
import zio.test._

import zio.console.Console

import datavault.service.{Command, Csv, Archive, Cli, Table, Repository}
import datavault.service.{CommandResult}

object UnitTest extends DefaultRunnableSpec with Fixture {

  val deps =
    Console.live >+> Cli.live >+> Csv.live >+> Archive.live >+> Table.live >+> Repository.live >+> Command.live

  def run(args1: Array[String]): ZIO[Any, Throwable, CommandResult] =
    Command.execute(args1).provideLayer(deps)

  def cli(args: Array[String]) = Cli.parseAndShowUsage(args).provideLayer(deps)

  def spec = suite("Cli Unit tests")(
    cliFixtures.map {
      case (title, Nil, result) =>
        testM(s"""${title} should fail""") {
          assertM(cli(Array()))(equalTo(result))
        }

      case (title, cmd :: tail, result) =>
        testM(s"""${cmd} ${title}: ${tail.mkString(",")}""") {
          assertM(cli((cmd :: tail).toArray))(equalTo(result))
        }
    }: _*
  )

  /*

  def spec = suite("A Suite")(
    test("A passing test") {
      assert(true)(isTrue)
    },
    test("A passing test run for JVM only") {
      assert(true)(isTrue)
    } @@ jvmOnly, //@@ jvmOnly only runs tests on the JVM
    test("A passing test run for JS only") {
      assert(true)(isTrue)
    } @@ jsOnly, //@@ jsOnly only runs tests on Scala.js
    test("A passing test with a timeout") {
      assert(true)(isTrue)
    } @@ timeout(10.nanos), //@@ timeout will fail a test that doesn't pass within the specified time
    test("A failing test... that passes") {
      assert(true)(isFalse)
    } @@ failing, //@@ failing turns a failing test into a passing test
    test("A ignored test") {
      assert(false)(isTrue)
    } @@ ignore, //@@ ignore marks test as ignored
    test("A flaky test that only works on the JVM and sometimes fails; let's compose some aspects!") {
      assert(false)(isTrue)
    } @@ jvmOnly           // only run on the JVM
      @@ eventually        //@@ eventually retries a test indefinitely until it succeeds
      @@ timeout(20.nanos) //it's a good idea to compose `eventually` with `timeout`, or the test may never end
  ) @@ timeout(60.seconds)   //apply a timeout to the whole suite
   */
}
