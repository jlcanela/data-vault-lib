package datavault.service

import zio._
import zio.macros.accessible

import java.nio.file.Path

import org.json4s.Formats._
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._

import Models.{Hubs, Source}

@accessible
object Repository {

  // Service definition
  trait Service {
    
      def loadHubs(path: Path) : ZIO[FileSystem, Throwable, Hubs] 
      def saveHubs(hubs: Hubs, path: Path) : ZIO[FileSystem, Throwable, Unit]
      def loadSource(path: Path) : ZIO[FileSystem, Throwable, Source] 
      def saveSource(source: Source, path: Path) : ZIO[FileSystem, Throwable, Unit]
  }

  // Module implementation
  val repository: ZLayer[FileSystem, Nothing, Repository] = ZLayer.succeed {
    new Service {
      
      implicit val formats = Serialization.formats(NoTypeHints)
        
      def loadHubs(path: Path) : ZIO[FileSystem, Throwable, Hubs] = for {
        content <- FileSystem.readFile(path)
      } yield read[Hubs](content)

      def writeHubs(hubs: Hubs): java.io.BufferedWriter => zio.ZIO[Any,Throwable,Unit] = (w: java.io.BufferedWriter) => ZIO.effect(writePretty(hubs, w))

      def saveHubs(hubs: Hubs, path: Path): ZIO[FileSystem, Throwable, Unit] =
        FileSystem.saveContent(path)(writeHubs(hubs))
       
      def loadSource(path: Path) : ZIO[FileSystem, Throwable, Source] = 
        FileSystem.readFile(path).map(read[Source])

      def writeSource(source: Source): java.io.Writer => ZIO[Any, Nothing, Unit] = (w: java.io.Writer) => ZIO.effectTotal(writePretty(source, w))
      
      def saveSource(source: Source, path: Path): ZIO[FileSystem, Throwable, Unit] = 
        FileSystem.saveContent(path)(writeSource(source))

      // def saveSource(source: Source, path: Path): ZIO[Any, Throwable, Unit] = 
      //  saveContent(path)(writeSource(source))

    }
  }
}
