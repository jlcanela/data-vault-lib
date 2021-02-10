package datavault.service

import zio._
import zio.console.Console

import java.io.{BufferedReader, InputStream, InputStreamReader}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.DataTypes.StringType

object Csv {

  type TableInfo = (String, StructType)

  // Service definition
  trait Service {
    def schema(fi: Archive.FileInfo): ZIO[Console, Throwable, TableInfo]
  }

  // Module implementation
  val live: ZLayer[Console, Nothing, Csv] = ZLayer.succeed {
    new Service {
      def schema(fi: Archive.FileInfo): ZIO[Console, Throwable, TableInfo] = for {
        line <- firstLine(fi._2)
        st = line
          .split(",")
          .map(_.replaceAll("\"", ""))
          .foldLeft(new StructType())((st, h) => st.add(h, StringType))
      } yield (fi._1.getName, st)

      def firstLine(is: InputStream): ZIO[Any, Nothing, String] = ZIO.effectTotal(
        new BufferedReader(new InputStreamReader(is))
          .lines()
          .findFirst()
          .orElse("")
      )

    }
  }

  def schema(fi: Archive.FileInfo): ZIO[Csv with Console, Throwable, TableInfo] =
    ZIO.accessM(_.get.schema(fi))
}
