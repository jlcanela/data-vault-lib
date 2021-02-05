package datavault.archive

import java.io.InputStream
import java.nio.file.Path

import datavault.io.FileSystem
import java.util.zip.ZipEntry

trait Visitor {
  def visit(info: FileInfo): Unit
}

trait Archive {
  def path: Path
  def stream: Stream[FileInfo]
}
