package datavault.extractor

import datavault.archive.Archive
import java.io.File
import datavault.archive.Visitor
import datavault.archive.FileInfo
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files

case class FileExtractionStatus(filename: String, length: Long, error: Option[String])

object FileExtractionStatus {
  implicit def ordering[A <: FileExtractionStatus]: Ordering[A] = Ordering.by(_.toString)
}

object Extractor {

  def extract(archive: Archive, destination: File): List[FileExtractionStatus] = {

    var status: List[FileExtractionStatus] = Nil

    val path: Path = Paths.get(destination.getPath())
    Files.createDirectories(path)
    
    archive.visit(new Visitor() {
      def visit(info: FileInfo) {
        val outputFile = new File(destination, info.filename)
        status = info.writeTo(outputFile).fold(
          error => FileExtractionStatus(info.filename, 0, Some(error)),
          Unit =>  FileExtractionStatus(info.filename, 0, None),
        ) :: status
      }
    })

    status

  }

  def listFiles(destination: File) = {
    destination.listFiles().map { file => FileExtractionStatus(file.getName(), file.length, None )}
  }  
}
