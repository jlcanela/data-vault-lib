package datavault.service

import zio._
import zio.stream._

import zio.console.Console

import org.apache.spark.sql.types.StructType

case class Source(tables: Map[String, TableInfo])
object Source {
  def apply(archive: String, seq: Seq[(String, StructType)]): Source = {
    val m = seq.map { case (name, struct) =>
      TableInfo(archive, name, struct)
    } map (m => (m.name, m))
    Source(tables = m.toMap)
  }
}

case class TableInfo(
    name: String,
    archive: String,
    path: String,
    columns: Seq[(String, String)]
)
object TableInfo {
  def apply(archive: String, name: String, struct: StructType): TableInfo = {
    TableInfo(
      name.replace(".csv", ""),
      archive,
      name,
      struct.fields.map(sf => (sf.name, sf.dataType.toString))
    )
  }
}

object Table {

  type TInfo = (String, StructType)

  // Service definition
  trait Service {
    def source: ZIO[Any, Throwable, ZSink[Any, Nothing, TInfo, TInfo, Source]]
  }

  // Module implementation
  val live: ZLayer[Console, Nothing, Table] = ZLayer.succeed {
    new Service {

      val emptyList                            = List[TInfo]()
      def addList(acc: List[TInfo], st: TInfo) = st :: acc

      def createSource: Sink[Nothing, TInfo, TInfo, Source] =
        ZSink.foldLeft(emptyList)(addList).map(Source("archive", _))

      def source: ZIO[Any, Throwable, ZSink[Any, Nothing, TInfo, TInfo, Source]] = ZIO.succeed(
        createSource
      )
    }
  }

  def source: ZIO[Table, Throwable, ZSink[Any, Nothing, TInfo, TInfo, Source]] =
    ZIO.accessM(_.get.source)
}
