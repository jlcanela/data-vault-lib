package datavault

import zio._

package object service {

  type Command = Has[Command.Service]
  type Models   = Has[Models.Service]
  type Repository  = Has[Repository.Service]

}
