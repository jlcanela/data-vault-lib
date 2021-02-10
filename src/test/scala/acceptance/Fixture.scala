package acceptance

trait Fixture {

  val inputFolder  = "src/test/resources/fixtures"
  val outputFolder = "out"

  val cmdNotRecognizedArgs = Array("")
  val cmdGenModelSuccessArgs =
    Array("gen-model", "-i", s"$inputFolder/test.zip", "-o", s"$outputFolder/model.json")
  val cmdExtractFilesSuccessArgs =
    Array("extract-files", "-i", s"$inputFolder/test.zip", "-o", s"$outputFolder/extract")
 val cmdGenHubconfigSuccessArgs =
   Array("gen-hubconfig", "-i", s"$inputFolder/model.json", "-o", s"$outputFolder/hubconfig.json")

}
