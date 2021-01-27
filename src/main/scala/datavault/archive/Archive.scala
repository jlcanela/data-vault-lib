package datavault.archive

import java.io.InputStream

trait FileInfo {
    def name: String
    def filename: String
    def inputStream: InputStream
    def firstLine: String
}

trait Visitor {
  def visit(info: FileInfo)
}

trait Archive {
  def name: String
  def visit(visitor: Visitor)
}

