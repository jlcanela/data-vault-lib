package datavault.archive

import datavault.io.FileSystem

import java.nio.file.{StandardCopyOption, Files, Path}
import java.io._
import java.util.zip._
import java.util.Optional

case class ZipFileInfo(ze: ZipEntry, is: InputStream) extends FileInfo {
  val _ext = ".csv"
  def name = if (filename.endsWith(_ext)) {
    filename.substring(0, filename.length() - _ext.length());
  } else {
    filename
  }

  def filename    = ze.getName()
  def size        = ze.getSize()
  def inputStream = is
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

  def stream: Stream[FileInfo] = {

    val zis: ZipInputStream = new ZipInputStream(FileSystem.open(path));
    var ze: ZipEntry        = null

    def nextStream: Stream[FileInfo] = {
      zis.getNextEntry() match {
        case null => {
          zis.close
          Stream.empty
        }
        case ze => Stream.cons(ZipFileInfo(ze, zis), nextStream)
      }
    }
    nextStream
  }

}
