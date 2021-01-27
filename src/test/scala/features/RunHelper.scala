package features

import picocli.CommandLine
import datavault.DataVaultCli
import datavault.extractor.Extractor
import java.io.File

import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.DefaultFormats
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._

import datavault.extractor.FileExtractionStatus

trait RunHelper {

  implicit val formats = Serialization.formats(NoTypeHints)

  def runCommand(command: String) =
    new CommandLine(new DataVaultCli()).execute(command.split(" ").tail: _*)

  def readFile(file: String) =
    scala.io.Source.fromFile(file).getLines().mkString("\n")

  def readResourceFile(file: String) =
    scala.io.Source.fromResource(file).getLines().mkString("\n")

  def listFiles(folder: String) = {
    Extractor.listFiles(new File(folder))
  }

  def readFileExtractionStatus(file: String) = {
    implicit val formats = DefaultFormats
    val content = readResourceFile(file)
    read[Array[FileExtractionStatus]](content)
  }

}
