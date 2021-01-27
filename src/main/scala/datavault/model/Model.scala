package datavault

import archive.Archive
import archive.Visitor
import archive.FileInfo

import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._
import java.io.File
import java.io.FileWriter

object Model {
  def fromArchive(archive: Archive) = {

    var tables: List[Table] = Nil

    archive.visit(new Visitor() {
      def visit(info: FileInfo) {
        val columns =  info.firstLine.split(",").toSeq.map(_.replaceAll("\"", "")).map(Column)
        tables = Table(info.name, archive.name, info.filename, columns) :: tables
      }
    })

    Model(tables.map(table => (table.name, table)).toMap)
  }

  implicit val formats = Serialization.formats(NoTypeHints)

  def save(model: Model, file: File) = {
    val writer = new FileWriter(file);
    writePretty(model, writer)
    writer.close()
  }

  def asJson(model: Model) = write(model)

  def diff(a: Model, b: Model): Diff = {
    val jsonA = parse(asJson(a))
    val jsonB = parse(asJson(b))
    jsonA diff jsonB
  }
}
case class Model(tables: Map[String, Table]) 

case class Table(
    name: String,
    archive: String,
    path: String,
    columns: Seq[Column]
)

case class Column(name: String)
