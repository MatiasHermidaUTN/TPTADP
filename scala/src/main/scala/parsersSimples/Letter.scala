package parsersSimples

import scala.util.Try

class Letter (input: String) extends Parser(input) {
  override def parsear(): Try[Any] = {
    if (!input.isEmpty && input.head.isLetter)
      Try(input.head)
    else
      Try(throw new Exception("No se encontro la letra"))
  }
}
