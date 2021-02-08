package datavault

import zio._

import service.{Archive, Cli, Csv, Command, Repository, Table}
import zio.console.Console

object App {
  val runtime = Runtime.default
// Archive: val live: ZLayer[Any, Nothing, Archive]
// Cli val live: ZLayer[Any, Nothing, Cli] = ZLayer.succeed {
  // Csv  val live: ZLayer[Any, Nothing, Csv]
  // Table val live: ZLayer[Console with Archive with Csv, Nothing, Table]
  // Command: live: ZLayer[Cli with Archive with Table with Csv with Console,
  val deps1 =
    Console.live >+> Archive.live >+> Cli.live >+> Csv.live >+> Table.live >+> Repository.live
  val deps                                                = deps1 >+> Command.live //>+> Command.command
  def app(args: Array[String]): ZIO[Any, Throwable, Unit] = Command.execute(args).provideLayer(deps)

  def unsafeExtract[A: Tagged](zlayer: ZLayer[Any, Nothing, Has[A]]): A =
    Runtime.default.unsafeRun(zlayer.build.use(ZIO.succeed(_))).get

  def main(args: Array[String]): Unit = {
    unsafeExtract(zio.clock.Clock.live)

    runtime.unsafeRun(app(args))
  }
}
