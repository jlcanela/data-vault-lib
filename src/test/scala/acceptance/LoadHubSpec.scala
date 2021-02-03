package features

import org.scalatest._
import flatspec._
import matchers.should.Matchers
import featurespec.AnyFeatureSpec

import picocli.CommandLine

import datavault._
import extractor._

class LoadHubSpec
    extends AnyFeatureSpec
    with Matchers
    with GivenWhenThen
    with Fixtures
    with RunHelper {

  feature("Load Hub") {

    scenario("Load Hub using a source file and model configuration") {

      Given(s"""a data model file '$loadHubDataModel'""")
      val command = loadHub

      And(s"""And a hub configuration '$loadHubConfig'""")

      When(s"""a "$command" command is run""")
      val exitCode = runCommand(command)

      Then(s"""execution is successful""")
      //withClue(s"return value exitCode =") { exitCode shouldBe Constants.Success }

      And(s"""all hub entries are present at the destination""")
      //val current: Array[FileExtractionStatus] = listFiles(staging)
      //val expected = readFileExtractionStatus(zipContent)
      //current.sorted should contain theSameElementsAs expected.sorted
    }
  }
}
