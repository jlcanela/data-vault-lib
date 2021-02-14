package datavault.acceptance

import zio._
import zio.test.Assertion._
import zio.test._

import datavault.Datavault

object CommandSpec extends DefaultRunnableSpec {
  val deps = Datavault.deps

  val SUCCESS                                            = 0
  val FAILURE                                            = 1
  def run(args: Array[String]): ZIO[Any, Throwable, Int] = Datavault.cmd(args).provideLayer(deps)

  def spec = suite("Command")(
    testM("unzip files must succeed") {
      assertM(run(Array("unzip-files", "data/archive.zip", "out/unzip")))(equalTo(SUCCESS))
    },
    testM("extract files must succeed") {
      assertM(run(Array("extract-files", "data/archive.zip", "out/extract")))(equalTo(SUCCESS))
    },
    testM("load hubs must succeed") {
      assertM(run(Array("load-hubs", "data/archive.zip", "out/hubs")))(equalTo(SUCCESS))
    }
  )
}
