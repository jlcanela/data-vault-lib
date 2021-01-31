package datavault.io

import java.io.{InputStream, OutputStream, BufferedWriter, OutputStreamWriter}
import java.nio.file.Path

trait FileSystem {
  def open(path: Path): InputStream
  def create(path: Path): OutputStream
}

object LocalFileSystem extends FileSystem {
  def open(path: Path) = java.nio.file.Files.newInputStream(path)
  def create(path: Path) = java.nio.file.Files.newOutputStream(path)
}

object FileSystem {

  def get(path: Path) = LocalFileSystem
  def open(path: Path) = {
    val fs = get(path)
    fs.open(path)
  }
  def readContent(path: Path) =  {
    val is: InputStream = FileSystem.open(path)
    val content = scala.io.Source.fromInputStream(is).getLines().mkString("\n")
    is.close()
    content
  }
  def create(path: Path) = {
    val fs = get(path)
    fs.create(path)
  }

  def writer(path: Path) = {
    val os: OutputStream = create(path)
    new BufferedWriter(new OutputStreamWriter(os))
  }

}