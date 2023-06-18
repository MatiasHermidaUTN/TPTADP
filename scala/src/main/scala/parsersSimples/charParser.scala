package parsersSimples

import scala.util.Try

class charParser(letra: Char) extends Parser {
  override def parsear(input: String): Try[Any] = {
    val anyChar = new anyChar
    anyChar.parsear(input).map {
      case character: Char if character == letra => character
      case _ => throw new Exception("No se encontro la letra")
    }
  }
}
