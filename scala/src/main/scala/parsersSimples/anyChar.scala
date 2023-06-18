package parsersSimples

import scala.util.Try

class anyChar extends Parser {
  override def parsear(input: String): Try[Any] = {
    Try(input.head)
  }
}
