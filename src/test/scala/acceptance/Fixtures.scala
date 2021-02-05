package features

trait Fixtures {

  val archive   = "test.zip"
  val folder    = "src/test/resources/fixtures"
  val modelFile = "out/model.json"
  val staging   = "out/staging"

  val hubConfigTemplatePath = "out/hub-config-template.json"

  def extractFiles     = s"./data-vault extract-files -i $folder/$archive -o $staging"
  def generateRawModel = s"./data-vault generate-model-file -i $folder/$archive -o $modelFile"
  def generateHubConfigTemplate =
    s"./data-vault generate-hub-config -i $sourceModelFile -o $hubConfigTemplatePath"

  val archiveDataModel = "fixtures/archive.zip-raw-data-model.json"
  def expectedRawDataModel =
    scala.io.Source.fromResource(archiveDataModel).getLines().mkString("\n")

  val zipContent      = "fixtures/archive.zip-content.json"
  val sourceModelFile = s"src/test/resources/$archiveDataModel"

  val hubConfigTemplate = "fixtures/hub-config-template.json"
  def expectedHubConfigTemplate =
    scala.io.Source.fromResource(hubConfigTemplate).getLines().mkString("\n")

  val testResources    = "src/test/resources"
  val loadHubDataModel = s"$testResources/$archiveDataModel"
  val hubConfig = "fixtures/hub.json"
  val loadHubConfig    = s"$testResources/$hubConfig"
  def loadHub          = s"./data-vault load-hubs -m $loadHubDataModel -c $loadHubConfig -i $folder/$archive -o out/hub"

}
