package datavault.service

import zio._
import zio.stream._
import zio.console.Console

import java.io.InputStream
import java.util.zip._

object Archive {

  type FileInfo = (ZipEntry, InputStream)

  // Service definition
  trait Service {
    def readCsvTableInfo(
        is: InputStream
    ): ZIO[Csv, Nothing, ZStream[Csv with Console, Throwable, Csv.TableInfo]]
  }

  // Module implementation
  val live: ZLayer[Csv with Console, Nothing, Archive] = ZLayer.succeed {
    new Service {

      def nextFileInfo(zis: ZipInputStream): ZIO[Any, Throwable, Option[FileInfo]] = ZIO.effect(
        zis.getNextEntry() match {
          case null => None
          case ze   => Some((ze, zis))
        }
      )

      def genSchema(zis: ZipInputStream): ZIO[Csv with Console, Throwable, Option[Csv.TableInfo]] =
        for {
          fi: Option[FileInfo] <- nextFileInfo(zis)
          ti <- fi match {
            case None => ZIO.succeed(None)
            case Some(fi) =>
              for {
                ti <- Csv.schema(fi)
              } yield Some(ti)
          }
        } yield ti

      def readCsvTableInfo(
          is: InputStream
      ): ZIO[Csv, Nothing, ZStream[Csv with Console, Throwable, Csv.TableInfo]] = for {
        zis <- ZIO.effectTotal(new ZipInputStream(is))
        stream = ZStream.repeatEffect(genSchema(zis)).takeWhile(_.isDefined).collect {
          case Some(ti) => ti
        }
      } yield stream

    }
  }

  def readCsvTableInfo(
      is: InputStream
  ): ZIO[Archive with Csv, Nothing, ZStream[Csv with Console, Throwable, Csv.TableInfo]] =
    ZIO.accessM(_.get.readCsvTableInfo(is))

}
