package unit

import datavault.service._

trait Fixture {

  val cliFixtures: Seq[(String, List[String], Cmd)] = Seq(
    ("fail with empty params", List(), ErrorCmd),
    ("succeed", List("extract-files", "-i", "i", "-o", "o"), ExtractFiles("i", "o")),
    ("fail", List("extract-files", "-i", "data/archive.zip"), ErrorCmd),
    ("succeed", List("gen-model", "-i", "i", "-o", "o"), GenerateModelFile("i", "o")),
    ("fail", List("gen-model", "-i", "data/archive.zip"), ErrorCmd),
    ("succeed", List("gen-hubconfig", "-i", "i", "-o", "o"), GenerateHubConfigFile("i", "o")),
    ("fail", List("gen-hubconfig", "-i", "i"), ErrorCmd),
    ("succeed", List("load-hubs", "-i", "i", "-o", "o"), LoadHubs("model", "config", "i", "o")),
    ("fail", List("load-hubs", "-i", "i"), ErrorCmd)
  )

  /*
        sealed trait Cmd
case class GenerateModelFile(input: String, output: String) extends Cmd {
  def inputPath: Path  = Paths.get(input)
  def outputPath: Path = Paths.get(output)
}
case class GenerateHubConfigFile(input: String, output: String)                   extends Cmd
case class ExtractFiles(input: String, output: String)                            extends Cmd
case class LoadHubs(model: String, config: String, input: String, output: String) extends Cmd
object ErrorCmd    */
}
