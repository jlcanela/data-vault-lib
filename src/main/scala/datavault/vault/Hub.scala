package datavault.vault

import datavault.model.{Model}
import datavault.service.Models._
import datavault.archive.{Archive, FileInfo, Visitor}
import datavault.file.CsvFile

import java.nio.file.Path

case class ReadFile(archive: String, path: String, table: String)

case class WriteTable(table: String)

object Hub {

  type FileGenerator = (Archive, Path) => zio.Task[Unit]

  def generateHeader(tableName: String, key: Column) =
    Array(s"HUB_${tableName.toUpperCase}_KEY", key.name.toUpperCase, "HUB_Load_DTS", "HUB_Rec_SRC")
  def generateRow(tableName: String, src: String, key: Column) = {
    val loadDts: String = (new java.util.Date).getTime().toString
    (key: String) => Array(key, key, loadDts, src)
  }

  def generateHub(
      prefix: Option[String],
      suffix: Option[String],
      info: FileInfo,
      hub: Hub
  ) = {
    println(s"${info.name}:${hub.key}")

    def writeFile(header: Array[String], stream: Stream[Array[String]]) = (path: Path) => {
      val writer = datavault.file.CsvFileWriter(header, stream)
      writer.writeTo(path)
    }

    def hubName(prefix: Option[String], suffix: Option[String], name: String) = {

      def stripPrefix(name: String) = prefix.map(name.stripPrefix).getOrElse(name)
      def stripSuffix(name: String) = suffix.map(name.stripSuffix).getOrElse(name)

      (stripPrefix _ andThen stripSuffix _)(name)
    }

    for {
      (header: Array[String], stream) <- CsvFile(info.inputStream).csvStream
      index: Int                  = header.indexOf(hub.key.name) if index >= 0
      row                         = generateRow(info.name, info.name, hub.key)
      hn                          = hubName(prefix, suffix, info.name)
      outputHeader: Array[String] = generateHeader(hn, hub.key)
      dataStream                  = stream.map(data => data(index)).map(row)
    } yield (writeFile(outputHeader, dataStream), s"${hn}.csv")

  }

  def process(source: Source, hubs: Hubs, archive: Archive) = {

    val tablesToProcess = hubs.hubs.map(_.table).toSet

    def generate(param: (FileInfo, Hub)) =
      generateHub(hubs.config.map(_.prefix), hubs.config.map(_.suffix), param._1, param._2)

    def haveHubDefinition(info: FileInfo) = tablesToProcess(info.name)
    def infoWithHub(info: FileInfo)       = hubs.withTableName(info.name).map(hub => (info, hub))

    val output = java.nio.file.Paths.get("out", "hub")
    java.nio.file.Files.createDirectories(output)

    def filesToProcess = archive.stream

    filesToProcess
      .filter(haveHubDefinition)
      .flatMap(infoWithHub)
      .map(generate)
      .map {
        case Some((g, name)) => {
          val path = output.resolve(name)
          println()
          g(path)
          s"generating ${path}"
        }
        case None => "skipped"
      }
      .foreach(println)
  }

}
