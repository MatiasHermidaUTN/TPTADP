package parsersSimples

import scala.util.Try

class Digit (input: String) extends Parser(input) {
  override def parsear(): Try[Any] = {
    if (!input.isEmpty && input.head.isDigit)
      Try(input.head)
    else
      Try(throw new Exception("No se encontro un digito"))
  }
}
