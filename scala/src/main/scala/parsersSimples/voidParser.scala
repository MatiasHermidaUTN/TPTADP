package parsersSimples

import scala.util.Try

case class voidParser() extends Parser {
  override def parsear(input: String): Try[Any] = {
    val anyChar = new anyChar
    anyChar.parsear(input).map( _ => () )
  }
}
