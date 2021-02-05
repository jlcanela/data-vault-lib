package datavault.service

import zio._
import zio.macros.accessible

@accessible
object Models {

  case class Source(tables: Map[String, Table])

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

  // Service definition
  trait Service {}

  // Module implementation
  val model: ZLayer[Any, Nothing, Models] = ZLayer.succeed {
    new Service {}
  }
}
