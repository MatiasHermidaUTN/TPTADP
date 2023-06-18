package parsersSimples

import scala.util.Try

class letter extends Parser {
  override def parsear(input: String): Try[Any] = {
    val anyChar = new anyChar
    anyChar.parsear(input).map {
      case letter: Char if letter.isLetter => letter
      case _ => throw new Exception("No se encontro la letra")
    }
  }
}
