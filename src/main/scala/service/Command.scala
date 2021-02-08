package datavault.service

import zio._
import zio.macros.accessible

import zio.stream._
import java.nio.file.{Files, Path, Paths}

import zio.console.Console

@accessible
object Command {

  //type MyDeps = Archive with Table with Console
  // type MyDeps = Archive with Table with Csv with Console
  // Service definition
  trait Service {
    def execute(
        cmd: Cmd
    ): ZIO[Archive with Table with Csv with Console with Repository, Throwable, Unit]
    def execute(
        args: Array[String]
    ): ZIO[Archive with Table with Cli with Csv with Console with Repository, Throwable, Unit]
  }

  // Module implementation
  val live: ZLayer[Archive with Table with Csv with Console with Repository, Nothing, Command] =
    ZLayer.succeed {
      new Service {

        def genModel(
            input: Path,
            output: Path
        ): ZIO[Archive with Table with Csv with Console with Repository, Nothing, Unit] = for {
          _           <- console.putStrLn("gen model v1")
          inputStream <- IO(Files.newInputStream(input)).orDie
          stream      <- Archive.readCsvTableInfo(inputStream)
          source      <- stream.run(Table.source).orDie
          os          <- IO(Files.newOutputStream(output)).orDie
          _           <- Repository.saveSource(source, os).orDie
        } yield ()

        def execute(
            cmd: Cmd
        ): ZIO[Archive with Table with Csv with Console with Repository, Throwable, Unit] =
          cmd match {
            case gmf: GenerateModelFile => genModel(gmf.inputPath, gmf.outputPath)
            case _                      => console.putStrLn("execute")
          }

        def execute(
            args: Array[String]
        ): ZIO[Archive with Table with Cli with Csv with Console with Repository, Throwable, Unit] =
          for {
            cmd <- Cli.parseAndShowUsage(args)
            _   <- execute(cmd)
          } yield ()
      }
    }
}
