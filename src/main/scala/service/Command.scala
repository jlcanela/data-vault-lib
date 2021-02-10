package datavault.service

import zio._

import java.nio.file.{Files, Path}

import zio.console.Console

sealed trait CommandResult
object CommandNotRecognized                 extends CommandResult
case class GenModelResult(success: Boolean) extends CommandResult

object Command {

  //type MyDeps = Archive with Table with Console
  // type MyDeps = Archive with Table with Csv with Console
  // Service definition
  trait Service {
    def execute(
        cmd: Cmd
    ): ZIO[Archive with Table with Csv with Console with Repository, Throwable, CommandResult]
    def execute(
        args: Array[String]
    ): ZIO[
      Archive with Table with Cli with Csv with Console with Repository,
      Throwable,
      CommandResult
    ]
  }

  // Module implementation
  val live: ZLayer[Archive with Table with Csv with Console with Repository, Nothing, Command] =
    ZLayer.succeed {
      new Service {

        def genModel(
            input: Path,
            output: Path
        ): ZIO[Archive with Table with Csv with Console with Repository, Throwable, CommandResult] =
          for {
            _           <- console.putStrLn("gen model v1")
            inputStream <- IO(Files.newInputStream(input))
            stream      <- Archive.readCsvTableInfo(inputStream)
            sink        <- Table.source
            source      <- stream.run(sink)
            os          <- IO(Files.newOutputStream(output))
            _           <- Repository.saveSource(source, os)
          } yield GenModelResult(true)

        def execute(
            cmd: Cmd
        ): ZIO[Archive with Table with Csv with Console with Repository, Throwable, CommandResult] =
          cmd match {
            case gmf: GenerateModelFile => genModel(gmf.inputPath, gmf.outputPath)
            case _                      => ZIO.succeed(CommandNotRecognized)
          }

        def execute(
            args: Array[String]
        ): ZIO[
          Archive with Table with Cli with Csv with Console with Repository,
          Throwable,
          CommandResult
        ] =
          for {
            cmd    <- Cli.parseAndShowUsage(args)
            result <- execute(cmd)
          } yield result
      }
    }

  def execute(
      args: Array[String]
  ): ZIO[
    Command with Archive with Table with Cli with Csv with Console with Repository,
    Throwable,
    CommandResult
  ] =
    ZIO.accessM(_.get.execute(args))
}
