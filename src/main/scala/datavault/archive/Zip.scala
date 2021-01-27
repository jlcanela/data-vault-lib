package datavault.archive

import java.nio.file.Path
import java.io.InputStream
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry
import java.io.File
import java.nio.file.Files
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Optional

trait CsvFile {
  def inputStream: InputStream

  def firstLine: String = {
    val br = new BufferedReader(new InputStreamReader(inputStream))
    val line: Optional[String] = br.lines().findFirst()
    line.orElse("")
  }

}

case class ZipFileInfo(ze: ZipEntry, zis: ZipInputStream) extends FileInfo with CsvFile {
  val _ext = ".csv"
  def name = if (filename.endsWith(_ext)) {
    filename.substring(0, filename.length() - _ext.length());
  } else {
    filename
  }

  def filename = ze.getName()
  def inputStream = zis
}

case class ZipArchive(zipFile: File) extends Archive {

  def name = zipFile.getPath()

  def visit(visitor: Visitor) = {
    val zis: ZipInputStream = new ZipInputStream(
      new FileInputStream(zipFile)
    );

    var ze: ZipEntry = zis.getNextEntry();

    while (ze != null) {

      val fileName = ze.getName();

      val info = ZipFileInfo(ze, zis)
      visitor.visit(info)
      ze = zis.getNextEntry()
    }

    zis.closeEntry()
    zis.close()

  }
}
