package datavault.extractor

import java.io.File
import java.nio.file.{Files, Path, Paths}

import datavault.archive.{Archive, FileInfo, Visitor}

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
      destination: Path
  ): List[Status] = {

    var status: List[Status] = Nil

    try {
      Files.createDirectories(destination)

      archive.visit(new Visitor() {
        def visit(info: FileInfo) {
          
          val outputPath = destination.resolve(info.filename)

          status = info
            .writeTo(outputPath)
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
