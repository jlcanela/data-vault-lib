package datavault.service

import zio._
import zio.macros.accessible

import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._

import datavault.archive.Archive
import datavault.file.CsvFile

@accessible
object Models {

  implicit val formats = Serialization.formats(NoTypeHints)

  case class Source(tables: Map[String, Table])

  object Source {
      implicit val formats = Serialization.formats(NoTypeHints)

      def apply(archive: Archive): Source = {
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

  case class Table(
    name: String,
    archive: String,
    path: String,
    columns: Seq[Column]
  )

  case class Column(name: String)

  case class Hub(name: String, table: String, source: String, key: Column)
  case class HubsConfig(prefix: String, suffix: String)

  case class Hubs(config: Option[HubsConfig], hubs: Seq[Hub]) {
    def tablesToProcess             = hubs.map(_.table).toSet
    def withTableName(name: String) = hubs.find(_.table == name)
  }

  object Hubs {

    val R = "olist_([a-z]*)?s_dataset".r
    def detectName(table: String) = table match {
      case R(name) => Some(name)
      case _       => None
    }

    def detectKey(name: String, keys: Seq[Column]) =
      keys.find(_.name == s"${name}_id")

    def fromTable(table: Table) = for {
      name <- detectName(table.name)
      key  <- detectKey(name, table.columns)
    } yield Hub(name, table.name, table.path, key)

    def fromSource(source: Source) = Hubs(None, source.tables.values.toSeq.flatMap(fromTable))

  }
  // Service definition
  trait Service {}

  // Module implementation
  val model: ZLayer[Any, Nothing, Models] = ZLayer.succeed {
    new Service {}
  }
}
