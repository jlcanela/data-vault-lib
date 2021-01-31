package datavault

import java.io.File
import java.math.BigInteger
import java.nio.file.{Files, FileSystem, Path}
import java.security.MessageDigest

import picocli.CommandLine
import picocli.CommandLine._

import zio._

object DataVault {
  def main(args: Array[String]): Unit = {
    val cmd = Cli.parse(args)
    val task: Task[_] = DVCommand.toZio(cmd)
    val runtime = Runtime.default

    runtime.unsafeRun(task)

  }
}
