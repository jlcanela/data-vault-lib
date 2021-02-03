package datavault

import java.nio.file.Path

import datavault.archive.ZipArchive
import datavault.extractor.Extractor
import datavault.model.{HubConfig, Model}
import org.yaml.snakeyaml.scanner.Constant

object Command {

  import zio._

  def genModel(input: Path, output: Path) = for {
    _     <- ZIO.effect(println(s"Running app  $input -> $output"))
    model <- Model.withArchive(new ZipArchive(input))
    _     <- ZIO.effect(Model.save(model, output))
    _     <- ZIO.effect(println(s"Done! "))
  } yield ()

  def extractFiles(input: Path, output: Path) = {
    val archive    = new ZipArchive(input)
    val result     = Extractor.extract(archive, output)
    val errorFound = result.exists(_.error.isDefined)
  }

  def generateHubConfigTemplate(input: Path, output: Path) = {
    val model             = Model.load(input)
    val hubConfigTemplate = HubConfig.fromModel(model)
    HubConfig.save(hubConfigTemplate, output)
  }

  def loadHubs(model: Path, config: Path, input: Path, output: Path) = {
    val m       = Model.load(model)
    val c       = HubConfig.load(config)
    val archive = ZipArchive(input)
    vault.Hub.process(m, c, archive)
  }

  import zio._

  def toZio(cmd: Cmd): Task[_] = cmd match {
    case GenerateModelFile(input, output) => genModel(input, output)
    case ExtractFiles(input, output)      => ZIO.effect(extractFiles(input, output))
    case GenerateHubConfigFile(input, output) =>
      ZIO.effect(generateHubConfigTemplate(input, output))
    case LoadHubs(model, config, input, output) =>
      ZIO.effect(loadHubs(model, config, input, output))
    case NoCmd => Task.none
  }

}
