package features

import org.scalatest.FeatureSpec
import org.scalatest.Matchers
import org.scalatest.GivenWhenThen
import datavault.DataVault
import picocli.CommandLine
import datavault.DataVaultCli
import datavault.Constants

class ExtractRawDataModelFromZipArchive
    extends FeatureSpec
    with Matchers
    with GivenWhenThen
    with Fixtures {

  def runCommand(command: String) = new CommandLine(new DataVaultCli()).execute(command.split(" ").tail: _*)

  def readFile(file: String) = scala.io.Source.fromFile(file).getLines().mkString("\n")

  feature("Extract a raw data model from a zip data archive") {

    scenario("Successfully extract csv files from a zip data archive") {

      Given(s"""a zip archive "$archive"  in folder "$folder"""")
      val command = extractFiles

      When(s"""a "$command" command is run""")
      val exitCode = runCommand(command)

      Then(s"""execution is successful""")
      withClue(s"return value exitCode =") { exitCode shouldBe Constants.NotImplemented }
      // And Then(s"""files are created""")
    }

    scenario("Successfully extract a raw data model from a zip data archive") {

      Given(s"""a zip archive "$archive"  in folder "$folder"""")
      val command = extractRawModel
     
      When(s"""a "$command" command is run""")
      val exitCode = runCommand(command)

      Then(s"""execution is successful""")
      withClue(s"return value exitCode =") { exitCode shouldBe Constants.Success }

      And(s"""a json model file "$modelFile" is created""")
      val current = readFile(modelFile)

      And(s""""$modelFile" have expected content""")
      val expected = sampleRawDataModel
      withClue(s"$modelFile should be expected model =") { current shouldBe expected} 
    
    }
  }
}
