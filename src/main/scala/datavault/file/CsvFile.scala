package datavault.file

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.util.Optional
    
import com.fasterxml.jackson.dataformat.csv._
import com.fasterxml.jackson.databind.{ObjectWriter}

import datavault.archive.FileInfo

trait Processor {
  def start(headers: Array[String]): Unit
  def row(line: Array[String]): Unit
  def end: Unit
}

case class CsvFileWriter(header: Array[String], stream: Stream[Array[String]]) {
  def writeTo(path: java.nio.file.Path) = {

    // initialize and configure the mapper
    val mapper = new CsvMapper();

    // initialize the schema
    val builder = header.foldLeft(CsvSchema.builder())((builder, name) => builder.addColumn(name))
    val schema  = builder.build

    // map the bean with our schema for the writer
    val writer: ObjectWriter = mapper.writerFor(classOf[Array[String]])

    import collection.JavaConverters._
    val iter: java.lang.Iterable[Array[String]] = Stream.cons(header, stream).toIterable.asJava
    writer.writeValues(path.toFile()).writeAll(iter);
  }
}

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
