package datavault.service

import zio._
import zio.stream._

import zio.console.Console

import zio.macros.accessible

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.DataType

@accessible
object Table {

  type TInfo       = (String, StructType)

  case class Source(tables: Map[String, TableInfo])
  object Source {
    def apply(archive: String, seq: Seq[(String, StructType)]): Source = {
      val m = seq.map {
        case (name, struct) => TableInfo(archive, name, struct)
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
      TableInfo(name.replace(".csv", ""), archive, name, struct.fields.map(sf => (sf.name, sf.dataType.toString)))
    }
  }

  // Service definition
  trait Service {
    def showFileInfo(fi: Archive.FileInfo): ZIO[Console, Nothing, Unit]
    def fileNames: ZSink[Any, Nothing, Archive.FileInfo, Archive.FileInfo, List[String]]
    def source: ZSink[Csv with Console, Nothing, TInfo, TInfo, Source]
  }

  // Module implementation
  val live: ZLayer[Console with Archive with Csv, Nothing, Table] = ZLayer.succeed {
    new Service {

      def showFileInfo(fi: Archive.FileInfo): ZIO[Console, Nothing, Unit] =
        console.putStrLn(fi._1.toString)

      def fileNames: ZSink[Any, Nothing, Archive.FileInfo, Archive.FileInfo, List[String]] =
        ZSink.foldLeft[Archive.FileInfo, List[String]](List[String]())(
          (acc: List[String], fi: Archive.FileInfo) => fi._1.getName :: acc
        )

      val emptyList                                    = List[TInfo]()
      def addList(acc: List[TInfo], st: TInfo) = st :: acc

      def createSource: Sink[Nothing, TInfo, TInfo, Source] =
        ZSink.foldLeft(emptyList)(addList).map(Source("archive", _))

      def source: ZSink[Csv with Console, Nothing, TInfo, TInfo, Source] =
        createSource
    }
  }
}
