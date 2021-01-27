package datavault

import java.io.File
import java.nio.file.Files

import java.math.BigInteger

import java.security.MessageDigest

import java.util.concurrent.Callable

import picocli.CommandLine
import picocli.CommandLine._

@Command(
  name = "datavault",
  mixinStandardHelpOptions = true,
  version = Array("datavault 1.0"),
  description = Array(
    "DataVault is a data migration tool built upon DataVault 2.0 recommandations."
  )
)
class DataVaultCli extends Callable[Int] {

  @Parameters(
    index = "0",
    description = Array("The command to execute: [ExtractRawModel]")
  )
  private var command: String = null

  @Option(
    names = Array("-i", "--input-file"),
    required = true,
    description = Array("The input file to process, format supported: ZIP")
  )
  private var inputFile: File = null

  @Option(
    names = Array("-o", "--output-file"),
    required = true,
    description = Array("The output file to generate")
  )
  private var outputFile: File = null

  def commandNotRecognized(command: String) = {
    println(s"command '$command' not recognized")
    Constants.CommandNotRecognized
  }

  override def call(): Int = command match {
    case "extract-files" =>
      Command.extractFiles(input = inputFile, output = outputFile)
    case "generate-raw-model" =>
      Command.generateRawModel(input = inputFile, output = outputFile)
    case "generate-hub-config-template" =>
      Command.generateHubConfigTemplate(input = inputFile, output = outputFile)
    case _ => commandNotRecognized(command)
  }
}
object DataVault extends App {
  val exitCode = new CommandLine(new DataVaultCli()).execute(args: _*)
  System.exit(exitCode)
}
