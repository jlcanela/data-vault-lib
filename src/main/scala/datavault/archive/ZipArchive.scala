package datavault.archive

import datavault.io.FileSystem

import java.nio.file.{StandardCopyOption, Files, Path}
import java.io._
import java.util.zip._
import java.util.Optional

case class ZipFileInfo(ze: ZipEntry, zis: ZipInputStream) extends FileInfo {
  val _ext = ".csv"
  def name = if (filename.endsWith(_ext)) {
    filename.substring(0, filename.length() - _ext.length());
  } else {
    filename
  }

  def filename    = ze.getName()
  def inputStream = zis
  def writeTo(target: Path): Either[String, Unit] = {

    try {
      Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING)
      Right(())
    } catch {
      case ex: Exception => Left(s"${ex.getClass().getName()}:${ex.getMessage()}")
    }
    
  }
}

case class ZipArchive(path: Path) extends Archive {

  def visit(visitor: Visitor) = {
    val zis: ZipInputStream = new ZipInputStream(
      FileSystem.open(path)
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
