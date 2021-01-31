package datavault

import scopt.OParser

import java.nio.file.{Path, Paths}

/*
 override def call(): Int = command match {
    case "extract-files" =>
      DVCommand.extractFiles(input = inputFile, output = outputFile)
    case "generate-raw-model" =>
      DVCommand.generateRawModel(input = inputFile, output = outputFile)
    case "generate-raw-model-f" =>
      DVCommand.generateRawModelF(input = inputFile, output = outputFile)
    case "generate-hub-config-template" =>
      DVCommand.generateHubConfigTemplate(input = inputFile, output = outputFile)
    case "load-hub" =>
      DVCommand.loadHubs(modelFile = dataModelFile, hubFile = hubConfigFile)
*/

sealed trait Cmd
object NoCmd extends Cmd
case class GenerateModelFile(input: Path, output: Path) extends Cmd 
case class GenerateHubConfigFile(input: Path, output: Path) extends Cmd 
case class ExtractFiles(input: Path, output: Path) extends Cmd 
case class LoadHubs(input: Path, output: Path) extends Cmd

case class DataVaultCli(cmd: String = null, input: Path = null, output: Path = null) {
    def toCmd = cmd match {
        case "generate-model-file" => GenerateModelFile(input, output)
        case "extract-files" => ExtractFiles(input, output)
        case "generate-hub-config" => GenerateHubConfigFile(input, output)
        case "load-hubs" => LoadHubs(input, output)
        case _ => NoCmd
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
            .action((_, c) => c.copy(cmd = "load-hubs")),       
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

    def parse(args: Array[String]): Cmd = OParser.parse(parser1, args, DataVaultCli()).map(_.toCmd).getOrElse(NoCmd)

}

