package datavault

import zio._

package object extractload {
  type ExtractLoad = Has[ExtractLoad.Service]
}
