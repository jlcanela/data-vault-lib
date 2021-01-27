package features

trait Fixtures {

    val archive = "archive.zip"
    val folder = "data"
    val modelFile = "out/model.json"
    val staging = "out/staging"

    def extractFiles = s"./data-vault extract-files -i $folder/$archive -o $staging"
    def generateRawModel = s"./data-vault generate-raw-model -i $folder/$archive -o $modelFile"

    val archiveDataModel = "fixtures/archive.zip-raw-data-model.json"
    def sampleRawDataModel = scala.io.Source.fromResource(archiveDataModel).getLines().mkString("\n")

    val zipContent = "fixtures/archive.zip-content.json"
    
}
