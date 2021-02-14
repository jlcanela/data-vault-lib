package datavault.cli

import zio._
import java.nio.file.Paths

sealed trait CommandParam
case class ErrorCommandParam(message: String) extends CommandParam
case class ExtractCommandParam(input: String, output: String) extends CommandParam {
    def inputPath = ZIO.succeed(Paths.get(input))
    def outputPath = ZIO.succeed(Paths.get(output))
}
case class UnzipCommandParam(input: String, output: String) extends CommandParam {
    def inputPath = ZIO.succeed(Paths.get(input))
    def outputPath = ZIO.succeed(Paths.get(output))
}

object Cli {
    def parse(args: Array[String]): CommandParam = args.toList match {
        case "extract-files" :: _ => ExtractCommandParam("data/archive.zip", "out/extract")
        case "unzip-files" :: _ => UnzipCommandParam("data/archive.zip", "out/unzip")
        case List() => ErrorCommandParam("no parameter")
        case _ => ErrorCommandParam("unknown parameter")
    }
}

