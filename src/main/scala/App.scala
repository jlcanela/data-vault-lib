package datavault

import zio._

import service.{Archive, Cli, Csv, Command, Repository, Table}
import zio.console.Console

// @scala.scalanative.reflect.annotation.EnableReflectiveInstantiation
object MyApp {

  def main(args: Array[String]): Unit = {

    val runtime = Runtime.default
    val deps =
      Console.live >+> Cli.live >+> Csv.live >+> Archive.live >+> Table.live >+> Repository.live >+> Command.live

    val result = runtime.unsafeRun(Command.execute(args).provideLayer(deps))
    println(result)

  }
}
