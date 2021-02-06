package datavault

import zio._

package object service {

  type Command = Has[Command.Service]
  type FileSystem = Has[FileSystem.Service]
  type Models   = Has[Models.Service]
  type Repository  = Has[Repository.Service]

}
