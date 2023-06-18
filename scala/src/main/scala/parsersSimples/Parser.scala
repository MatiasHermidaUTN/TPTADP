package parsersSimples

import scala.util.Try

abstract class Parser {
  def parsear(input: String): Try[Any]

  def <|> (p2: Parser): Parser = {
    val p1 = this
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p1.parsear(input) orElse p2.parsear(input)
      }
    }
  }
}
