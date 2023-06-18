package parsersSimples

import scala.util.Try

class digit extends Parser {
  override def parsear(input: String): Try[Any] = {
    val anyChar = new anyChar
    anyChar.parsear(input).map {
      case digit: Char if digit.isDigit => digit
      case _ => throw new Exception("No se encontro un digito")
    }
  }
}
