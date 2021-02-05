package features

import org.scalatest._
import flatspec._
import matchers.should._
import featurespec.AnyFeatureSpec

import picocli.CommandLine

import datavault.{DataVault, DataVaultCli}

class GenerateRawDataModelFromZipArchive
    extends AnyFeatureSpec
    with Matchers
    with GivenWhenThen
    with Fixtures
    with RunHelper {

  feature("Generate a raw data model from a zip data archive") {

    scenario("Successfully generates a raw data model from a zip data archive") {

      Given(s"""a zip archive "$archive"  in folder "$folder"""")
      val ouputFilename = modelFile
      And(s"""an output filename "${ouputFilename}"""")
      val command = generateRawModel

      When(s"""a "$command" command is run""")
      val exitCode = runCommand(command)

      Then(s"""execution is successful""")
      //withClue(s"return value exitCode =") { exitCode shouldBe Constants.Success }

      And(s"""a json model file "$modelFile" is generated""")
      val current = readFile(modelFile)

      And(s""""$modelFile" have expected content""")
      val expected = expectedRawDataModel
      withClue(s"$modelFile should be expected model =") { current shouldBe expected }

    }
  }
}
