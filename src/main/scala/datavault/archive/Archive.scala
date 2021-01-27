package datavault.archive

import java.io.InputStream
import java.io.File

trait FileInfo {
    def name: String
    def filename: String
    def inputStream: InputStream
    def firstLine: String
    def writeTo(file: File): Either[String, Unit]
}

trait Visitor {
  def visit(info: FileInfo)
}

trait Archive {
  def name: String
  def visit(visitor: Visitor)
}

