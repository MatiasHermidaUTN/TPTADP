package parsersSimples

import scala.util.Try

class Void(input: String) extends Parser(input) {
  override def parsear(): Try[Any] = {
    if (!input.isEmpty)
      Try(())
    else
      Try(throw new Exception("Input vacío"))
  }
}
