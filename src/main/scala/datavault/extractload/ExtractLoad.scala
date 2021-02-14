package datavault.extractload

import zio._
import zio.stream._
import zio.console.Console

import java.util.zip._
import java.io.InputStream
import java.nio.file.{Files, Path, Paths}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

import com.fasterxml.jackson.dataformat.csv._
import com.fasterxml.jackson.databind.{MappingIterator, ObjectWriter}

object ExtractLoad {

    final case class Table(headers: Array[String], rows: Stream[Any, Array[String]]) {
      def writer: ObjectWriter = {
        val mapper = new CsvMapper();
        val builder = headers.foldLeft(CsvSchema.builder())((builder, name) => builder.addColumn(name))
        val schema  = builder.setUseHeader(true).build

        mapper.writer(schema) // when no header, could use mapper.writerFor(classOf[Array[String]])
      }

      import collection.JavaConverters._
      def asJavaIterator(it: Iterator[Either[Any,Array[String]]]) = it.collect {
        case Right(value: Array[String]) => value
      }.toIterable.asJava

      def write(path: Path): ZIO[Any, Throwable, Unit] = for {
        _ <- IO(Files.createDirectories(path.getParent()))
        _ <- rows.toIterator.use( (iter: Iterator[Either[Any,Array[String]]]) => {
            ZIO.effect(writer.writeValues(path.toFile()).writeAll( asJavaIterator(iter)))
        })
      } yield ()
        
    }

    final case class File(inputStream: InputStream, name: String, path: String) {
      def asTable: ZIO[Any, Throwable, Table] = {
        
        def it : MappingIterator[Array[String]]= {
          val mapper = new CsvMapper();
          mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
          mapper.readerFor(classOf[Array[String]]).readValues(inputStream)
        }

        def headers(it: MappingIterator[Array[String]]): Array[String] = 
          if (it.hasNext) {
            it.next
          } else {
            throw new Exception("no header in CSV file")
          }
        
        def effect(it: MappingIterator[Array[String]]) : ZIO[Any, Option[Nothing], Array[String]] = ZIO.fromOption( 
        if (it.hasNext) {
          Some(it.next)
        } else {
          None
        })

        for {
          it <- ZIO.succeed(it)
          h: Array[String] <- ZIO.effect(headers(it))
          stream = Stream.repeatEffectOption(effect(it))
        } yield Table(h, stream)
     
      }
    }

  // Service definition
  trait Service {
      def archiveFiles(path: Path): ZIO[Console, Throwable, ZStream[Console, Throwable, File]] 
      def unzipFiles(path: Path, destination: Path): ZIO[Console, Throwable, Unit]
      def extractFiles(path: Path, destination: Path): ZIO[Console, Throwable, Unit]
      def tables(path: Path): ZIO[Console, Throwable, ZStream[Console, Throwable, Table]] 
      def table(inputStream: InputStream): ZIO[Any, Throwable, Table]
  }

  // Module implementation
  val live: ZLayer[Any, Nothing, ExtractLoad] = ZLayer.succeed {
    new Service {

      def createStream[T](path: Path, processFile: File => ZIO[Console, Throwable, T]): ZIO[Console, Throwable, ZStream[Console, Throwable, T]] = {

        def zipInputStream = for {
          is <-  IO(Files.newInputStream(path))
          zis <- IO(new ZipInputStream(is))
        } yield zis

        def nextFileInfo(zis: ZipInputStream): ZIO[Console, Option[Throwable], File] = for {
          fe <- ZIO.fromOption(Option(zis.getNextEntry()))
        } yield File(zis, fe.getName, fe.getName)

        for {
          zis <- zipInputStream
          effect = for {
            file <- nextFileInfo(zis)
            r <- processFile(file).mapError(f => Some(f))
          } yield r
          stream = Stream.repeatEffectOption(effect)
        } yield stream
      }

      def archiveFiles(path: Path): ZIO[Console, Throwable, ZStream[Console, Throwable, File]] = 
        createStream(path, (file: File) => IO(file))

      def unzipFiles(path: Path, destination: Path): ZIO[Console, Throwable, Unit] = {

        def writeFile(file: File) = for {
          _ <- IO(Files.createDirectories(destination))
          target <- IO(destination.resolve(Paths.get(file.name)))
          _ <- IO(Files.copy(file.inputStream,target, REPLACE_EXISTING))
           _ <- zio.console.putStrLn(s"written $target")
        } yield ()

        for {
          stream <- createStream(path, writeFile)
          _ <- stream.run(Sink.drain)
        } yield ()
        
      }

      def extractFiles(path: Path, destination: Path): ZIO[Console, Throwable, Unit] = {

        def saveTable(file: File) = for {
          table <- file.asTable
          _ <- table.write(destination.resolve(file.path))
        } yield ()

        for {
           _ <- IO(Files.createDirectories(destination))
          stream <- createStream(path, saveTable)
           _ <- stream.run(Sink.drain)
        } yield ()
      }

      def tables(path: Path): ZIO[Console, Throwable, ZStream[Console, Throwable, Table]] = {

        def asTable(file: File) = for {
          table <- file.asTable
        } yield table

        for {
          stream <- createStream(path, asTable)
        } yield stream
      }
        
      def table(inputStream: InputStream): ZIO[Any, Throwable, Table] = 
      File(inputStream, "name", "path").asTable
    }
  }
  def archiveFiles(path: Path): ZIO[ExtractLoad with Console, Throwable, ZStream[Console, Throwable, File]] =
    ZIO.accessM(_.get.archiveFiles(path))

  def tables(path: Path): ZIO[ExtractLoad with Console, Throwable, ZStream[Console, Throwable, Table]] =
    ZIO.accessM(_.get.tables(path))

  def table(inputStream: InputStream): ZIO[ExtractLoad, Throwable, Table] = 
    ZIO.accessM(_.get.table(inputStream))

  def unzipFiles(path: Path, destination: Path): ZIO[ExtractLoad with Console, Throwable, Unit] =
    ZIO.accessM(_.get.unzipFiles(path, destination))

  def extractFiles(path: Path, destination: Path): ZIO[ExtractLoad with Console, Throwable, Unit] =
    ZIO.accessM(_.get.extractFiles(path, destination))
}
