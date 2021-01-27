package features

import org.scalatest.FeatureSpec
import org.scalatest.Matchers
import org.scalatest.GivenWhenThen
import datavault.DataVault
import picocli.CommandLine
import datavault.DataVaultCli
import datavault.Constants

class GenerateRawDataModelFromZipArchive
    extends FeatureSpec
    with Matchers
    with GivenWhenThen
    with Fixtures
    with RunHelper {

  feature("Generate a raw data model from a zip data archive") {

    scenario("Successfully generates a raw data model from a zip data archive") {

      Given(s"""a zip archive "$archive"  in folder "$folder"""")
      val command = generateRawModel
     
      When(s"""a "$command" command is run""")
      val exitCode = runCommand(command)

      Then(s"""execution is successful""")
      withClue(s"return value exitCode =") { exitCode shouldBe Constants.Success }

      And(s"""a json model file "$modelFile" is generated""")
      val current = readFile(modelFile)

      And(s""""$modelFile" have expected content""")
      val expected = sampleRawDataModel
      withClue(s"$modelFile should be expected model =") { current shouldBe expected} 
  
    }
  }
}
