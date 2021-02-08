package datavault.service

import org.rogach.scallop._

class Conf(args: Seq[String], onError: (Throwable, Scallop) => Unit) extends ScallopConf(args) {
  version("datavault.lib 1.0.0 (c) 2021 Jean-Luc CANELA")
  banner("""Usage: datavault.lib [gen-model|gen-hubconfig|extract-files|load-hubs] ...
           |datavault.lib is a data conversion tool
           |Options:
           |""".stripMargin)
  footer("\nFor more information, consult https://jlcanela.github.io/data-vault-lib/.")

  val genModel = new Subcommand("gen-model") with WithInputOutput {
    val input = opt[String](required = true, name = "input", descr = "path to archive file")
    val output =
      opt[String](required = true, name = "output", descr = "path of model file to generate")
  }
  addSubcommand(genModel)

  val genHubConfig = new Subcommand("gen-hubconfig") with WithInputOutput {
    val input  = opt[String](name = "input", descr = "path to archive file")
    val output = opt[String](name = "output", descr = "path of hub config file to generate")
  }
  addSubcommand(genHubConfig)

  val extractFiles = new Subcommand("extract-files") with WithInputOutput {
    val input = opt[String](required = true, name = "input", descr = "path to archive file")
    val output = opt[String](
      required = true,
      name = "output",
      descr = "path of extracted data destination folder "
    )
  }
  addSubcommand(extractFiles)

  val loadHubs = new Subcommand("load-hubs") with WithInputOutput {
    // TODO: add model: Path and config: Path
    val input  = opt[String](name = "input", descr = "path to archive file")
    val output = opt[String](name = "output", descr = "path of hub data destination folder ")
  }
  addSubcommand(loadHubs)

  requireSubcommand()
  verify()

  override def onError(e: Throwable): Unit = onError(e, builder)
}
