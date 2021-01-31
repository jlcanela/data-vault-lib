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


object Model {

  implicit val formats = Serialization.formats(NoTypeHints)

  def load(path: Path) = {
    val content = FileSystem.readContent(path)
    val model   = read[Model](content)
    model
  }

  def fromArchive(archive: Archive) = {

    var tables: List[Table] = Nil

    archive.visit(new Visitor() {
      def visit(info: FileInfo) {
        val columns = CsvFile(info.inputStream).firstLine
          .split(",")
          .toSeq
          .map(_.replaceAll("\"", ""))
          .map(Column)
        tables = Table(info.name, archive.path.toString, info.filename, columns) :: tables
      }
    })

    Model(tables.map(table => (table.name, table)).toMap)
  }

  def withArchive(archive: Archive) = {
    import zio._
    //Model.withArchive(new ZipArchive(input)
    val model = fromArchive(archive)
    ZIO.succeed(model)
  }

  def save(model: Model, path: Path, pretty: Boolean = true) = {
    val writer = FileSystem.writer(path)
    writePretty(model, writer)
    writer.close()
  }

  /*
  def asJson(model: Model) = write(model)

  def diff(a: Model, b: Model): Diff = {
    val jsonA = parse(asJson(a))
    val jsonB = parse(asJson(b))
    jsonA diff jsonB
  }*/

}
case class Model(tables: Map[String, Table])

case class Table(
    name: String,
    archive: String,
    path: String,
    columns: Seq[Column]
)

case class Column(name: String)

