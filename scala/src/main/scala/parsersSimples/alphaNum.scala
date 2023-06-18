package parsersSimples

import scala.util.Try

class alphaNum extends Parser {
  override def parsear(input: String): Try[Any] = {
    val letter = new letter
    val digit = new digit
    letter.parsear(input) orElse digit.parsear(input)
  }
}
