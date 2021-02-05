package datavault.model

import java.io._
import java.nio.file.Path

import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._

import datavault.io.FileSystem
import datavault.service.Models._

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

  def fromSource(source: Source) = Hubs(None, source.tables.values.toSeq.flatMap(fromTable))

}
