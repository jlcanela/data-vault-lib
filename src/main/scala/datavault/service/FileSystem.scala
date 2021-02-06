package datavault.service

import zio._
import zio.macros.accessible

import java.io._
import java.nio.file._

@accessible
object FileSystem {

  // Service definition
  trait Service {
    
    def open(path: Path): ZIO[Any, Throwable, InputStream]
    def create(path: Path): ZIO[Any, Throwable, OutputStream]
    def writer(path: Path) : ZIO[Any, Throwable, Writer]

    def readFile(path: Path) : ZIO[Any, Throwable, String]
    def saveContent(path: Path)(use: BufferedWriter => ZIO[Any, Throwable, Unit]) 
     
  }

  // Module implementation
  val fileSystem: ZLayer[Any, Nothing, FileSystem] = ZLayer.succeed {
    new Service {

      def open(path: Path): ZIO[Any, Throwable, InputStream] =
        ZIO.effect(java.nio.file.Files.newInputStream(path))

      def create(path: Path): ZIO[Any, Throwable, OutputStream] = 
        ZIO.effect(java.nio.file.Files.newOutputStream(path))

      def writer(path: Path): ZIO[Any, Throwable, BufferedWriter] = for {
          os <- create(path)
      } yield new BufferedWriter(new OutputStreamWriter(os))

      def readFile(path: Path): ZIO[Any, Throwable, String] = for {
        is <- open(path)
        content = scala.io.Source.fromInputStream(is).getLines().mkString("\n")
      } yield content

      def closeWriter(w: Writer): URIO[Any, Any] = ZIO.effectTotal(w.close)

      def saveContent(path: Path)(use: BufferedWriter => ZIO[Any, Throwable, Unit]) = //:  Writer => ZIO[Any, Throwable, Unit] => zio.ZIO[Any,Throwable, Unit]= 
           writer(path).bracket(closeWriter _)(use)
     
    }
  }
}
