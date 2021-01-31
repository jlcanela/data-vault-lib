package datavault.archive

import java.io.InputStream
import java.nio.file.Path

import datavault.io.FileSystem

trait FileInfo {
  def name: String
  def filename: String
  def inputStream: InputStream
  def writeTo(path: Path): Either[String, Unit]
}