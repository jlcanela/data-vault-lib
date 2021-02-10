package datavault.service

import zio._
//import zio.macros.accessible

import java.io.{InputStream, OutputStream, BufferedWriter, OutputStreamWriter, Writer}

import org.json4s.Formats._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, writePretty}

//import Table.{Source}

//@accessible
object Repository {

  // Service definition
  trait Service {

    // def loadHubs(path: Path) : ZIO[FileSystem, Throwable, Hubs]
    // def saveHubs(hubs: Hubs, path: Path) : ZIO[FileSystem, Throwable, Unit]
    def loadSource(is: InputStream): ZIO[Any, Throwable, Source]
    def saveSource(source: Source, os: OutputStream): ZIO[Any, Throwable, Unit]
  }

  // Module implementation
  val live: ZLayer[Any, Nothing, Repository] = ZLayer.succeed {
    new Service {

      implicit val formats = Serialization.formats(NoTypeHints)

      /*
      def loadHubs(path: Path) : ZIO[Any, Throwable, Hubs] = for {
        content <- FileSystem.readFile(path)
      } yield read[Hubs](content)

      def writeHubs(hubs: Hubs): java.io.BufferedWriter => zio.ZIO[Any,Throwable,Unit] = (w: java.io.BufferedWriter) => ZIO.effect(writePretty(hubs, w))

      def saveHubs(hubs: Hubs, path: Path): ZIO[FileSystem, Throwable, Unit] =
        FileSystem.saveContent(path)(writeHubs(hubs))
       */

      def writer(os: OutputStream): ZIO[Any, Nothing, BufferedWriter] =
        ZIO.effectTotal(new BufferedWriter(new OutputStreamWriter(os)))

      def closeWriter(w: Writer): URIO[Any, Any] = ZIO.effectTotal(w.close)

      def saveContent(
          os: OutputStream
      )(
          use: BufferedWriter => ZIO[Any, Throwable, Unit]
      ) = //:  Writer => ZIO[Any, Throwable, Unit] => zio.ZIO[Any,Throwable, Unit]=
        writer(os).bracket(closeWriter _)(use)

      def loadSource(is: InputStream): ZIO[Any, Throwable, Source] =
        ZIO.effect(read[Source](is))

      def writeSource(source: Source)(writer: java.io.Writer): ZIO[Any, Nothing, Unit] =
        ZIO.effectTotal(
          writePretty(source, writer).close()
        )

      def saveSource(source: Source, os: OutputStream): ZIO[Any, Throwable, Unit] =
        saveContent(os)(writeSource(source))

    }
  }

  def saveSource(source: Source, os: OutputStream): ZIO[Repository, Throwable, Unit] =
    ZIO.accessM(_.get.saveSource(source, os))
}
