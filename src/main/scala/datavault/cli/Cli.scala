package datavault.cli

import zio._
import java.nio.file.Paths

trait CreatePath {

  def input: String
  def output: String

  def inputPath = ZIO
    .fromOption(Option(Paths.get(input)))
    .mapError(_ => new IllegalArgumentException("invalid input"))
  def outputPath = ZIO
    .fromOption(Option(Paths.get(output)))
    .mapError(_ => new IllegalArgumentException("invalid output"))
}

sealed trait CommandParam
final case class ErrorCommandParam(message: String) extends CommandParam
final case class ExtractCommandParam(input: String, output: String)
    extends CommandParam
    with CreatePath
final case class UnzipCommandParam(input: String, output: String)
    extends CommandParam
    with CreatePath
final case class LoadHubCommandParam(input: String, output: String)
    extends CommandParam
    with CreatePath

object Cli {
  def parse(args: Array[String]): CommandParam = args.toList match {
    case "extract-files" :: _ => ExtractCommandParam("data/archive.zip", "out/extract")
    case "unzip-files" :: _   => UnzipCommandParam("data/archive.zip", "out/unzip")
    case "load-hubs" :: _     => LoadHubCommandParam("data/archive.zip", "out/hubs")
    case List()               => ErrorCommandParam("no parameter")
    case _                    => ErrorCommandParam("unknown parameter")
  }
}
