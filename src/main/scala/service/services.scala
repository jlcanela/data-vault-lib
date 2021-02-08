package datavault

import zio._

package object service {

  type Archive    = Has[Archive.Service]
  type Cli        = Has[Cli.Service]
  type Command    = Has[Command.Service]
  type Csv        = Has[Csv.Service]
  type Repository = Has[Repository.Service]
  type Table      = Has[Table.Service]
}
