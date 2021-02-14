package datavault

import zio._

package object transform {
  type Datavault = Has[Datavault.Service]
}
