package datavault.service

import zio._
import zio.macros.accessible

import java.nio.file.Path

import org.json4s.Formats._
import org.json4s.native.Serialization.{read, write, writePretty}

import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write, writePretty}
import org.json4s.native.JsonMethods._


import datavault.io.FileSystem

import Models.{Hubs, Source}

@accessible
object Repository {

  // Service definition
  trait Service {
    
      def loadHubs(path: Path) : ZIO[Any, Throwable, Hubs] 
      def saveHubs(hubs: Hubs, path: Path) : Task[Unit]
      def loadSource(path: Path) : ZIO[Any, Throwable, Source] 
      def saveSource(source: Source, path: Path) : Task[Unit]
  }

  // Module implementation
  val repository: ZLayer[Any, Nothing, Repository] = ZLayer.succeed {
    new Service {
      
      implicit val formats = Serialization.formats(NoTypeHints)

      def readFile(path: Path) = ZIO.effect({
        val is      = FileSystem.open(path)
        scala.io.Source.fromInputStream(is).getLines().mkString("\n")
      })

      def saveContent(path: Path): ZIO.BracketRelease[Any,Throwable, java.io.BufferedWriter] = ZIO.effect(FileSystem.writer(path)).bracket(is => ZIO.effectTotal(is.close))

      def loadHubs(path: Path) : ZIO[Any, Throwable, Hubs] = readFile(path).map(read[Hubs])

      def saveHubs(hubs: Hubs, path: Path): Task[Unit] = saveContent(path)(f => ZIO.effect(writePretty(hubs, f)))
       
      def loadSource(path: Path) : ZIO[Any, Throwable, Source] = readFile(path).map(read[Source])

      def saveSource(source: Source, path: Path) : Task[Unit] = saveContent(path)(f => ZIO.effect(writePretty(source, f)))

    }
  }
}
