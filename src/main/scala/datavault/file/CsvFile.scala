package datavault.file

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.util.Optional
    
import com.fasterxml.jackson.dataformat.csv._
import com.fasterxml.jackson.databind.{ObjectWriter}

import datavault.archive.FileInfo

case class CsvFile(inputStream: InputStream) {

  def firstLine: String = {
    val br                     = new BufferedReader(new InputStreamReader(inputStream))
    val line: Optional[String] = br.lines().findFirst()
    line.orElse("")
  }

  def csvStream: Option[(Array[String], Stream[Array[String]])] = {

    val mapper = new CsvMapper();

    mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

    val it = mapper.readerFor(classOf[Array[String]]).readValues(inputStream);

    if (!it.hasNext) {
      return None
    }

    val headers: Array[String] = it.next

    def stream: Stream[Array[String]] = if (it.hasNext) {
      Stream.cons(it.next, stream)
    } else {
      Stream.empty
    }

    Some((headers, stream))

  }

}
