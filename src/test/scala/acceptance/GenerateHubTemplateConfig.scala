package features

import org.scalatest._
import flatspec._
import matchers.should.Matchers
import featurespec.AnyFeatureSpec

import picocli.CommandLine

import datavault.DataVault
import datavault.DataVaultCli
import datavault.Constants
import datavault.extractor.Extractor
import datavault.extractor.FileExtractionStatus
import org.json4s.Diff
import org.json4s.JsonAST.JNull
import org.json4s.JsonAST.JNothing
import org.json4s.native.Serialization.{read, write, writePretty}

class GenerateHubTemplateConfig
    extends AnyFeatureSpec
    with Matchers
    with GivenWhenThen
    with Fixtures
    with RunHelper {

  feature("Generate a Hub configuration template file") {

    scenario(
      "Successfully generate a hub configuration template file from a source model file"
    ) {

      Given(s"""a "source-model.json" model file""")
      val command = generateHubConfigTemplate

      When(s"""a "$command" command is run""")
      val exitCode = runCommand(command)

      Then(s"""execution is successful""")
      withClue(s"return value exitCode =") {
        exitCode shouldBe Constants.Success
      }

      And(s"""a json hub config file "$hubConfigTemplate" is generated""")
      val current = readFile(hubConfigTemplatePath)

      And(s"""a hub configuration file have the expected content""")
      val expected = expectedHubConfigTemplate

      val Diff(changed, added, deleted) = diffJson(current, expected)

      withClue(s"""|$hubConfigTemplatePath
                     | should be expected
                     | hub config template =""".stripMargin) {
        changed shouldBe JNothing
        added shouldBe JNothing
        deleted shouldBe JNothing
      }

    }
  }
}
