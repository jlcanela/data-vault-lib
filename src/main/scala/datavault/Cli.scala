package datavault

import scopt.OParser

import java.nio.file.{Path, Paths}

sealed trait Cmd
object NoCmd                                                              extends Cmd
case class GenerateModelFile(input: Path, output: Path)                   extends Cmd
case class GenerateHubConfigFile(input: Path, output: Path)               extends Cmd
case class ExtractFiles(input: Path, output: Path)                        extends Cmd
case class LoadHubs(model: Path, config: Path, input: Path, output: Path) extends Cmd

case class DataVaultCli(
    cmd: String = null,
    model: Path = null,
    config: Path = null,
    input: Path = null,
    output: Path = null
) {
  def toCmd = cmd match {
    case "generate-model-file" => GenerateModelFile(input, output)
    case "extract-files"       => ExtractFiles(input, output)
    case "generate-hub-config" => GenerateHubConfigFile(input, output)
    case "load-hubs"           => LoadHubs(model, config, input, output)
    case _                     => NoCmd
  }
}

object Cli {
  val parser1 = {
    val builder = OParser.builder[DataVaultCli]
    import builder._
    OParser.sequence(
      programName("datavault"),
      head("datavault", "1.0"),
      cmd("generate-model-file")
        .action((_, c) => c.copy(cmd = "generate-model-file")),
      //.text("generate model file."),
      cmd("extract-files")
        .action((_, c) => c.copy(cmd = "extract-files")),
      cmd("generate-hub-config")
        .action((_, c) => c.copy(cmd = "generate-hub-config")),
      cmd("load-hubs")
        .action((_, c) => c.copy(cmd = "load-hubs"))
        .children(
          opt[String]("model")
            .required()
            .abbr("m")
            .action((model, c) => c.copy(model = Paths.get(model)))
            .text("model"),
          opt[String]("config")
            .required()
            .abbr("c")
            .action((config, c) => c.copy(config = Paths.get(config)))
            .text("config")
        ),
      opt[String]("input")
        .required()
        .abbr("i")
        .action((x, c) => c.copy(input = Paths.get(x)))
        .text("input file to process"),
      opt[String]("output")
        .required()
        .abbr("o")
        .action((x, c) => c.copy(output = Paths.get(x)))
        .text("output file to process")
    )
  }

  def parse(args: Array[String]): Cmd =
    OParser.parse(parser1, args, DataVaultCli()).map(_.toCmd).getOrElse(NoCmd)

}
