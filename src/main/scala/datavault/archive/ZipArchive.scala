package datavault.archive

import java.nio.file.Path
import java.io._

import java.util.zip._
import java.util.Optional

import java.nio.file.Files

case class ZipFileInfo(ze: ZipEntry, zis: ZipInputStream) extends FileInfo {
  val _ext = ".csv"
  def name = if (filename.endsWith(_ext)) {
    filename.substring(0, filename.length() - _ext.length());
  } else {
    filename
  }

  def filename    = ze.getName()
  def inputStream = zis
  def writeTo(target: File): Either[String, Unit] = {

    val BUFSIZE = 4096
    val buffer  = new Array[Byte](BUFSIZE)

    def saveFile(fis: InputStream, fos: OutputStream) = {
      writeToFile(bufferReader(fis) _, fos)
      fos.close
    }

    def bufferReader(fis: InputStream)(buffer: Array[Byte]) =
      (fis.read(buffer), buffer)

    def writeToFile(
        reader: (Array[Byte]) => Tuple2[Int, Array[Byte]],
        fos: OutputStream
    ): Boolean = {
      val (length, data) = reader(buffer)
      if (length >= 0) {
        fos.write(data, 0, length)
        writeToFile(reader, fos)
      } else
        true
    }

    try {
      saveFile(inputStream, new FileOutputStream(target))
      Right(())
    } catch {
      case ex: Exception => Left(ex.getMessage())
    }
  }
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
