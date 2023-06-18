package parsersSimples

import scala.util.Try

case class anyChar() extends Parser {
  override def parsear(input: String): Try[Any] = {
    Try(input.head)
  }
}
