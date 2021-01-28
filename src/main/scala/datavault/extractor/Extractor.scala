package datavault.extractor

import datavault.archive.Archive
import java.io.File
import datavault.archive.Visitor
import datavault.archive.FileInfo
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files

sealed trait Status {
  def error: Option[String]
}

case class FileExtractionStatus(
    filename: String,
    length: Long,
    error: Option[String]
) extends Status

case class Error(message: String) extends Status {
  def error: Option[String] = Some(message)
}

object FileExtractionStatus {
  implicit def ordering[A <: FileExtractionStatus]: Ordering[A] =
    Ordering.by(_.toString)
}

object Extractor {

  def extract(
      archive: Archive,
      destination: File
  ): List[Status] = {

    var status: List[Status] = Nil

    val path: Path = Paths.get(destination.getPath())
    try {
      Files.createDirectories(path)

      archive.visit(new Visitor() {
        def visit(info: FileInfo) {
          val outputFile = new File(destination, info.filename)
          status = info
            .writeTo(outputFile)
            .fold(
              error => FileExtractionStatus(info.filename, 0, Some(error)),
              Unit => FileExtractionStatus(info.filename, 0, None)
            ) :: status
        }
      })

      status

    } catch {
      case ex: Exception => Error(ex.getMessage) :: Nil
    }

  }

  def listFiles(destination: File) = {
    destination.listFiles().map { file =>
      FileExtractionStatus(file.getName(), file.length, None)
    }
  }
}
