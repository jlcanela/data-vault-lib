package datavault.service

import java.nio.file.Path

import zio._
import zio.macros.accessible

import datavault.archive.ZipArchive
import datavault.extractor.Extractor
import datavault.service.Models.Hubs
import datavault.vault.Hub
import datavault._

@accessible
object Command {

  // Service definition
  trait Service {
    def genModel(input: Path, output: Path): ZIO[Repository with FileSystem, Throwable, Any]
    def extractFiles(input: Path, output: Path): Unit
    def generateHubConfigTemplate(input: Path, output: Path): ZIO[Repository with FileSystem, Throwable, Any]
    def loadHubs(model: Path, config: Path, input: Path, output: Path): ZIO[Repository with FileSystem, Throwable, Any]

    def toZio(cmd: Cmd): ZIO[Repository with FileSystem, Throwable, Any]
  }

  // Module implementation
  val command: ZLayer[Repository, Nothing, Command] = ZLayer.succeed {
    new Service {
     
      import datavault.service.Models.Source

      def genModel(input: Path, output: Path) : ZIO[Repository with FileSystem, Throwable, Any]= for {
        _     <- ZIO.effect(println(s"Running app  $input -> $output"))
        source = Source(new ZipArchive(input))
        _     <- Repository.saveSource(source, output)
        _     <- ZIO.effect(println(s"Done! "))
      } yield ()

      def extractFiles(input: Path, output: Path) = {
        val archive    = new ZipArchive(input)
        val result     = Extractor.extract(archive, output)
        val errorFound = result.exists(_.error.isDefined)
      }

      def generateHubConfigTemplate(input: Path, output: Path) : ZIO[Repository with FileSystem, Throwable, Unit]= for {
        source <- Repository.loadSource(input)
        hubs = Hubs.fromSource(source)
        _ <- Repository.saveHubs(hubs, output)
      } yield ()

      def loadHubs(model: Path, config: Path, input: Path, output: Path) : ZIO[Repository with FileSystem, Throwable, Unit]= for {
         m       <- Repository.loadSource(model)
         c       <- Repository.loadHubs(config)
         archive = ZipArchive(input)
        _ =  vault.Hub.process(m, c, archive)
      } yield ()

      import zio._

      def toZio(cmd: Cmd): ZIO[Repository with FileSystem, Throwable, Any] = cmd match {
        case GenerateModelFile(input, output) => genModel(input, output)
        case ExtractFiles(input, output)      => ZIO.effect(extractFiles(input, output))
        case GenerateHubConfigFile(input, output) => generateHubConfigTemplate(input, output)
        case LoadHubs(model, config, input, output) => loadHubs(model, config, input, output)
        case NoCmd => ZIO.effectTotal(())
      }
    }
  }
}
