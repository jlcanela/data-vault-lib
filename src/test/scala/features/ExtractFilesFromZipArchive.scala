package features

import org.scalatest.FeatureSpec
import org.scalatest.Matchers
import org.scalatest.GivenWhenThen

import picocli.CommandLine

import datavault.DataVault
import datavault.DataVaultCli
import datavault.Constants
import datavault.extractor.Extractor
import datavault.extractor.FileExtractionStatus

class ExtractFilesFromZipArchive
    extends FeatureSpec
    with Matchers
    with GivenWhenThen
    with Fixtures
    with RunHelper {

  feature("Extract all csv files locally from a zip data archive") {

    scenario("Successfully extract locally csv files from a zip data archive") {

      Given(s"""a zip archive "$archive"  in folder "$folder"""")
      val command = extractFiles

      And(s"""a "$staging" folder""")

      When(s"""a "$command" command is run""")
      val exitCode = runCommand(command)

      Then(s"""execution is successful""")
      withClue(s"return value exitCode =") { exitCode shouldBe Constants.Success }

      And(s"""all files are extracted in """)
      val current: Array[FileExtractionStatus] = listFiles(staging)
      val expected = readFileExtractionStatus(zipContent)
      current.sorted should contain theSameElementsAs expected.sorted
    }
  }
}


