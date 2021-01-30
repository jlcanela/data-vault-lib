package datavault.archive

import java.io.InputStream
import java.nio.file.Path

import datavault.io.FileSystem

trait Visitor {
  def visit(info: FileInfo)
}

trait Archive {
  def path: Path
  def visit(visitor: Visitor): Unit
}
