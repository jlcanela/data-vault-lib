package datavault.file

import com.fasterxml.jackson.dataformat.csv._
import com.fasterxml.jackson.databind.{ObjectWriter}

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
