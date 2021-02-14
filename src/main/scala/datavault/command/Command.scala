package datavault.command

import zio._
import zio.console._

import datavault.cli.{ExtractCommandParam, UnzipCommandParam}
import datavault.extractload.ExtractLoad

object Command {

  val SUCCESS = 0
  val NO_COMMAND = 1

  // Service definition
  trait Service {
      def showMessage(message: String): ZIO[Console, Nothing, Int]
      def unzipFiles(ucp: UnzipCommandParam): ZIO[ExtractLoad with Console, Throwable, Int]
      def extractFiles(ucp: ExtractCommandParam): ZIO[ExtractLoad with Console, Throwable, Int]
  }

  // Module implementation
  val live: ZLayer[Any, Nothing, Command] = ZLayer.succeed {
    new Service {
      def showMessage(message: String): ZIO[Console, Nothing, Int] = for {
        _ <- putStrLn(message)
      } yield NO_COMMAND

      def unzipFiles(ucp: UnzipCommandParam): ZIO[ExtractLoad with Console, Throwable, Int] = for {
        input <- ucp.inputPath
        output <- ucp.outputPath
        _ <- putStrLn(s"extract $input to $output")
        _ <- ExtractLoad.unzipFiles(input, output)
      } yield SUCCESS

      def extractFiles(ecp: ExtractCommandParam): ZIO[ExtractLoad with Console, Throwable, Int] = for {
        input <- ecp.inputPath
        output <- ecp.outputPath
        _ <- putStrLn(s"extract $input to $output")
        _ <- ExtractLoad.extractFiles(input, output)
      } yield SUCCESS
      
    }
  }

  def showMessage(message: String): ZIO[Command with Console, Nothing, Int] =
    ZIO.accessM(_.get.showMessage(message))

  def unzipFiles(ucp: UnzipCommandParam): ZIO[Command with ExtractLoad with Console, Throwable, Int] = 
    ZIO.accessM(_.get.unzipFiles(ucp))

  def extractFiles(ecp: ExtractCommandParam): ZIO[Command with ExtractLoad with Console, Throwable, Int] = 
    ZIO.accessM(_.get.extractFiles(ecp))
}
