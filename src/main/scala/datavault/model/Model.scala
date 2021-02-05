package datavault.model

import java.io._
import java.nio.file.Path

import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._

import datavault.archive.{Archive, FileInfo, Visitor}
import datavault.file.CsvFile
import datavault.io.FileSystem
import datavault.service.Models._

object Model {

  implicit val formats = Serialization.formats(NoTypeHints)

  def fromArchive(archive: Archive) = {
    val tables = archive.stream.map { info =>
      {
        val columns = CsvFile(info.inputStream).firstLine
          .split(",")
          .toSeq
          .map(_.replaceAll("\"", ""))
          .map(Column)
        Table(info.name, archive.path.toString, info.filename, columns)
      }
    }.toList

    Source(tables.map(table => (table.name, table)).toMap)
  }

}
