package datavault.service

import org.rogach.scallop._

import java.nio.file.{Path, Paths}

sealed trait Cmd
case class GenerateModelFile(input: String, output: String) extends Cmd {
  def inputPath: Path  = Paths.get(input)
  def outputPath: Path = Paths.get(output)
}
case class GenerateHubConfigFile(input: String, output: String)                   extends Cmd
case class ExtractFiles(input: String, output: String)                            extends Cmd
case class LoadHubs(model: String, config: String, input: String, output: String) extends Cmd
object ErrorCmd                                                                   extends Cmd

trait WithInputOutput { this: ScallopConf =>
  def input: ScallopOption[String]
  def output: ScallopOption[String]
}

import zio._
import zio.macros.accessible

import zio.console.Console

@accessible
object Cli {

  trait Service {
    def parseAndShowUsage(args: Array[String]): ZIO[Console, Throwable, Cmd]
  }

  def withInputOutput(wio: WithInputOutput) = for {
    input  <- wio.input.toOption
    output <- wio.output.toOption
  } yield (input, output)

  def extractFiles(conf: Conf) = for {
    (input, output) <- withInputOutput(conf.extractFiles)
  } yield ExtractFiles(input, output)

  def genModel(conf: Conf) = for {
    (input, output) <- withInputOutput(conf.extractFiles)
  } yield GenerateModelFile(input, output)

  def genHubConfig(conf: Conf) = for {
    (input, output) <- withInputOutput(conf.extractFiles)
  } yield GenerateHubConfigFile(input, output)

  def loadHubs(conf: Conf) = for {
    (input, output) <- withInputOutput(conf.extractFiles)
  } yield LoadHubs("model", "config", input, output)

  def cmd(conf: Conf) = conf.subcommand match {
    case Some(conf.genModel)     => genModel(conf)
    case Some(conf.genHubConfig) => genHubConfig(conf)
    case Some(conf.extractFiles) => extractFiles(conf)
    case Some(conf.loadHubs)     => loadHubs(conf)
    case _                       => None
  }

  val live: ZLayer[Any, Nothing, Cli] = ZLayer.succeed {
    new Service {

      def parse(args: Array[String]): ZIO[Any, String, Cmd] = IO.effectAsync[String, Cmd] {
        callback =>
          val conf =
            new Conf(args, (ex: Throwable, builder: Scallop) => callback(IO.fail(builder.help)))
          callback(IO.succeed(cmd(conf).getOrElse(ErrorCmd)))
      }

      def parseAndShowUsage(args: Array[String]): ZIO[Console, Throwable, Cmd] = parse(args)
        .tapError(err => console.putStrLn(err))
        .catchAll(_ => ZIO.succeed(ErrorCmd))
    }
  }
}
