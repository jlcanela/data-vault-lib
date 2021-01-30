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
import datavault.DVCommand
import datavault.io._

import java.nio.file.Path
import java.nio.file.FileSystems

class UnitTest extends AnyFlatSpec with Matchers {

  val defaultFS = FileSystems.getDefault()

  "ZipFileInfo" should "manage name without extension" in {
    val ze = new ZipEntry("zip-entry")
    val info = ZipFileInfo(ze, null)
    info.name shouldBe "zip-entry"
  }

  "ZipFileInfo" should "save file and overwrite file" in {
    val ze = new ZipEntry("zip-entry")
    val is = new ByteArrayInputStream(Array[Byte](0x65));
    val zis = new ZipInputStream(is)
    val info = ZipFileInfo(ze, zis)

    val fs: FileSystem = LocalFileSystem
    val path = defaultFS.getPath("out", "file");
    info.writeTo(path) shouldBe Right()
    info.writeTo(path) shouldBe Right()
  }

  "ZipFileInfo" should "manage gracefully save file errors" in {
    val ze = new ZipEntry("zip-entry")
    val is = new ByteArrayInputStream(Array[Byte]());
    val zis = new ZipInputStream(is)
    val info = ZipFileInfo(ze, zis)

    val fs: FileSystem = LocalFileSystem
    val path = defaultFS.getPath("dummy-folder", "file");
    info.writeTo(path) shouldBe Left("java.nio.file.NoSuchFileException:dummy-folder/file")
  }

  "CommandLine" should "manage command not recognized" in {
    val noCmd = datavault.Cli.parse(Array("no", "command"))
    noCmd shouldBe datavault.NoCmd
    val task: zio.Task[_] = DVCommand.toZio(noCmd)
    task shouldBe zio.Task.none
  }

  "Command.extractFiles" should "manage extraction error" in {
    val result = DVCommand.extractFiles(
      java.nio.file.Paths.get("src", "test", "resources", "fixtures", "table.zip"), 
      java.nio.file.Paths.get("README.md", "no-folder"))
    result shouldBe Constants.ExtractionError
  }
}
