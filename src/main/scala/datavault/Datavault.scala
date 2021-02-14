package datavault

import zio.console.Console

import cli.{Cli, ErrorCommandParam, ExtractCommandParam, UnzipCommandParam}
import command.Command
import extractload.ExtractLoad

object Datavault {
    
    def deps = Console.live >+> ExtractLoad.live >+> Command.live

    def cmd(args: Array[String]) = Cli.parse(args) match {
        case ErrorCommandParam(message) => Command.showMessage(message)
        case ucp: UnzipCommandParam => Command.unzipFiles(ucp)
        case ecp: ExtractCommandParam => Command.extractFiles(ecp)
        case _ => Command.showMessage("unknown command")
    }


    def run(args: Array[String]) = {
        val runtime = zio.Runtime.default
        val result = runtime.unsafeRun(cmd(args).provideLayer(deps))
        if (result > Command.NO_COMMAND) System.exit(result)
    }

    def main(args: Array[String]) = run(args)

}