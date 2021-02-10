package datavault.service

case class Hub(name: String, table: String, source: String, key: TableInfo.Column)
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

  def detectKey(name: String, keys: Seq[TableInfo.Column]) =
    keys.find(_._1 == s"${name}_id")

  def fromTableInfo(tableInfo: TableInfo) = for {
    name <- detectName(tableInfo.name)
    key  <- detectKey(name, tableInfo.columns)
  } yield Hub(name, tableInfo.name, tableInfo.path, key)

  def fromSource(source: Source) = Hubs(None, source.tables.values.toSeq.flatMap(fromTableInfo))

}
