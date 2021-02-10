package datavault

import zio._

package object service {

  type Repository = Has[Repository.Service]
  type Cli        = Has[Cli.Service]     // Console
  type Csv        = Has[Csv.Service]     // Console
  type Archive    = Has[Archive.Service] // Csv with Console
  type Table      = Has[Table.Service]   // Console
  type Command    = Has[Command.Service]
}
