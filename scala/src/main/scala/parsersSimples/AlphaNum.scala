package parsersSimples

import scala.util.Try

class AlphaNum (input: String) extends Parser(input) {
  override def parsear(): Try[Any] = {
    val letter = new Letter(input)
    val digit = new Digit(input)
    letter.parsear() orElse digit.parsear()
  }
}
