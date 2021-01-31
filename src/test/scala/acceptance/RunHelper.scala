package features

import java.io.File

import picocli.CommandLine

import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.DefaultFormats
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._

import datavault.DataVaultCli
import datavault.extractor.{Extractor, FileExtractionStatus}


trait RunHelper {

  implicit val formats = Serialization.formats(NoTypeHints)

  def parseCommand(command: String) = command.split(" ").tail
  def runCommand(command: String) = datavault.DataVault.main(parseCommand(command))

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

  def diffJson(current: String, expected: String): Diff = {
    val currentJson = parse(current)
    val expectedJson = parse(expected)
    expectedJson.diff(currentJson)
  }

}
