package datavault.file

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.util.Optional

import datavault.archive.FileInfo

case class CsvFile(inputStream: InputStream) {

  def firstLine: String = {
    val br                     = new BufferedReader(new InputStreamReader(inputStream))
    val line: Optional[String] = br.lines().findFirst()
    line.orElse("")
  }

}
