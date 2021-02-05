package datavault.model

import java.io._
import java.nio.file.Path

import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._

import datavault.io.FileSystem


case class Hub(name: String, table: String, source: String, key: Column)
case class HubsConfig(prefix: String, suffix: String)
case class Hubs(config: Option[HubsConfig], hubs: Seq[Hub]) {
   def tablesToProcess = hubs.map(_.table).toSet
   def withTableName(name: String) = hubs.find(_.table == name)
}

object HubConfig {

  implicit val formats = Serialization.formats(NoTypeHints)

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

  def fromModel(model: Model) = Hubs(None, model.tables.values.toSeq.flatMap(fromTable))

  def load(path: Path) = {
    val is        = FileSystem.open(path)
    val content   = scala.io.Source.fromInputStream(is).getLines().mkString("\n")
    val hubs = read[Hubs](content)
    hubs
  }

  def save(hubs: Hubs, path: Path) = {
    val writer = FileSystem.writer(path)
    writePretty(hubs, writer)
    writer.close()
  }

}
