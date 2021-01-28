package datavault.filesystem

import zio.nio.core.file.FileSystem

import java.io.File
import zio._
import zio.blocking.Blocking

object FS {

  def localFileSystem: ZIO[Blocking, Exception, FileSystem] =
    FileSystem.getFileSystem(new java.net.URI("file:."))
}
