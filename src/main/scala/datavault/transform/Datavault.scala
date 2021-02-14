package datavault.transform

import zio._
import zio.clock.Clock

import datavault.extractload.ExtractLoad.Table

object Datavault {

  // Service definition
  trait Service {
    def transformHub: ZIO[Clock, Throwable, (Table) => ZIO[Any, Option[Nothing], Table]]
    def filterHub: ZIO[Any, Nothing, (Table) => Boolean]
  }

  // Module implementation
  val live: ZLayer[Clock, Nothing, Datavault] = ZLayer.succeed {
    new Service {
      val R = "olist_([a-z]*)?s_dataset(\\.csv)?".r

      def entity(tableName: String) = tableName match {
        case R(name, _) => Some(name)
        case _          => None
      }

      def mapHubTable(clock: String, table: Table): Option[Table] = {
        val Table(name, headers, rows) = table

        def generateHubHeader(tableName: String, key: String) =
          Array(s"HUB_${tableName.toUpperCase}_KEY", key.toUpperCase, "HUB_Load_DTS", "HUB_Rec_SRC")

        def generateHubRow(index: Int)(arr: Array[String]) =
          Array(arr(index), arr(index), clock, name)

        for {
          entName <- entity(name)
          index = 0
        } yield Table(
          name,
          generateHubHeader(entName, s"${entName}_id"),
          rows.map(generateHubRow(index))
        )

      }

      def transformHub: ZIO[Clock, Throwable, (Table) => ZIO[Any, Option[Nothing], Table]] =
        for {
          clock <- zio.clock.currentDateTime
        } yield (table: Table) => ZIO.fromOption(mapHubTable(clock.toEpochSecond.toString, table))

      def filterHub: ZIO[Any, Nothing, (Table) => Boolean] = ZIO.succeed((table: Table) =>
        table.name match {
          case R(name, _) => true
          case _          => false
        }
      )
    }
  }

  def transformHub
      : ZIO[Datavault with Clock, Throwable, (Table) => ZIO[Any, Option[Nothing], Table]] =
    ZIO.accessM(_.get.transformHub)

  def filterHub: ZIO[Datavault, Nothing, (Table) => Boolean] =
    ZIO.accessM[Datavault](_.get.filterHub)

}
