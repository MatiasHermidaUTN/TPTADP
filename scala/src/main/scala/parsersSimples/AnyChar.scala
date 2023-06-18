package parsersSimples

import scala.util.Try

class AnyChar (input: String) extends Parser(input) {
  override def parsear(): Try[Any] = {
    if (!input.isEmpty)
      Try(input.head)
    else
      Try(throw new Exception("Input vacío"))
  }
}
