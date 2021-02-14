package datavault

import zio.console.Console
import zio.clock.Clock
import cli.{Cli, ErrorCommandParam, ExtractCommandParam, UnzipCommandParam}
import command.Command
import extractload.ExtractLoad
import datavault.cli.LoadHubCommandParam
import datavault.transform.{Datavault => DV}

object Datavault {

  def deps = Clock.live >+> Console.live >+> ExtractLoad.live >+> DV.live >+> Command.live

  def cmd(args: Array[String]) = Cli.parse(args) match {
    case ErrorCommandParam(message) => Command.showMessage(message)
    case ucp: UnzipCommandParam     => Command.unzipFiles(ucp)
    case ecp: ExtractCommandParam   => Command.extractFiles(ecp)
    case hcp: LoadHubCommandParam   => Command.loadHubs(hcp)
    case _                          => Command.showMessage("unknown command")
  }

  def run(args: Array[String]) = {
    val runtime = zio.Runtime.default
    val result  = runtime.unsafeRun(cmd(args).provideLayer(deps))
    if (result > Command.NO_COMMAND) System.exit(result)
  }

  def main(args: Array[String]) = run(args)

}
