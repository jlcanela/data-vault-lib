package features

trait Fixtures {
    
    val archive = "archive.zip"
    val folder = "data"
    val modelFile = "model.json"

    def extractFiles = s"./data-vault extract-files -i $folder/$archive -o $modelFile"
    def extractRawModel = s"./data-vault extract-raw-model -i $folder/$archive -o $modelFile"
}
