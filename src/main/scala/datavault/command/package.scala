package datavault

import zio._

package object command {
  type Command = Has[Command.Service]
}
