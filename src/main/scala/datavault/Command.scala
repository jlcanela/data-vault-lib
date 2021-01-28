package datavault

import java.io.File
import datavault.archive.ZipArchive
import datavault.extractor.Extractor

object Command {

  def generateRawModel(input: File, output: File) = {
    val archive = new ZipArchive(input)
    val model   = Model.fromArchive(archive)
    Model.save(model, output)
    Constants.Success
  }

  def extractFiles(input: File, output: File) = {
    val archive    = new ZipArchive(input)
    val result     = Extractor.extract(archive, output)
    val errorFound = result.exists(_.error.isDefined)
    if (errorFound) {
      Constants.ExtractionError
    } else {
      Constants.Success
    }
  }

  def generateHubConfigTemplate(input: File, output: File) = {
    val model             = Model.fromFile(input)
    val hubConfigTemplate = HubConfig.fromModel(model)
    HubConfig.save(hubConfigTemplate, output)
    Constants.Success
  }

}
