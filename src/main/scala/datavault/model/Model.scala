package datavault

import archive.Archive
import archive.Visitor
import archive.FileInfo

import file.CsvFile

import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._
import java.io.File
import java.io.FileWriter

object Model {

  implicit val formats = Serialization.formats(NoTypeHints)

  def fromFile(modelFile: File) = {
    val content = scala.io.Source.fromFile(modelFile).getLines().mkString("\n")
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
        tables = Table(info.name, archive.name, info.filename, columns) :: tables
      }
    })

    Model(tables.map(table => (table.name, table)).toMap)
  }

  def save(model: Model, file: File) = {
    val writer = new FileWriter(file);
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
) {}

case class Column(name: String)

case class Hub(name: String, table: String, source: String, key: Column)
case class HubConfig(hubs: Seq[Hub])

object HubConfig {

  implicit val formats = Serialization.formats(NoTypeHints)

  val R = "olist_([a-z]*)?s_dataset".r
  def detectName(table: String) = table match {
    case R(name) => Some(name)
    case _ => {
      println(s"""failed $table""")
      None
    }
  }

  def detectKey(name: String, keys: Seq[Column]) =
    keys.find(_.name == s"${name}_id")

  def fromTable(table: Table) = for {
    name <- detectName(table.name)
    key  <- detectKey(name, table.columns)
  } yield Hub(name, table.name, table.path, key)

  def fromModel(model: Model) = HubConfig(model.tables.values.toSeq.flatMap(fromTable))

  def save(hubConfig: HubConfig, file: File) = {
    val writer = new FileWriter(file);
    writePretty(hubConfig, writer)
    writer.close()
  }

}
