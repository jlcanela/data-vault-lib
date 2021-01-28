package unit

import collection.mutable.Stack
import org.scalatest._
import flatspec.AnyFlatSpec
import matchers.should._
import java.util.zip.ZipEntry
import datavault.archive.ZipFileInfo
import java.io.InputStream
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream
import picocli.CommandLine
import datavault.DataVaultCli
import datavault.Constants
import datavault.Command
import java.io.File

class UnitTest extends AnyFlatSpec with Matchers {

  "ZipFileInfo" should "manage name without extension" in {
    val ze = new ZipEntry("zip-entry")
    val info = ZipFileInfo(ze, null)
    info.name shouldBe "zip-entry"
  }

  "ZipFileInfo" should "manage gracefully save file errors" in {
    val ze = new ZipEntry("zip-entry")
    val is = new ByteArrayInputStream(Array[Byte]());
    val zis = new ZipInputStream(is)
    val info = ZipFileInfo(ze, zis)

    info.writeTo(new java.io.File("dummy-folder/file")) shouldBe Left("dummy-folder/file (No such file or directory)")
  }

  "CommandLine" should "manage command not recognized" in {
    val exitCode = new CommandLine(new DataVaultCli()).execute("-i", "input", "-o", "output", "unknown")
    exitCode shouldBe Constants.CommandNotRecognized
  }

  "Command.extractFiles" should "manage extration error" in {
    val result = Command.extractFiles(
      new File("src/test/resources/fixtures/table.zip"), 
      new File("README.md/no-folder"))
    result shouldBe Constants.ExtractionError
  }
}
