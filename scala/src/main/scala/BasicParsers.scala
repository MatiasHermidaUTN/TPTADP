import ExceptionsHelper.throwExceptionIfCondition
import Parser.{Parser, SuccessParse}

object BasicParsers {

  case class anyChar() extends Parser[Char]  {
    override def parseFunction(elementToParse: String): SuccessParse[Char] = {
      (elementToParse.charAt(0), elementToParse.substring(1))
    }
  }

  def char(charToFind: Char): Parser[Char] = anyChar().satisfies(_==charToFind)

  val void: Parser[Unit] = anyChar().const(())

  def matches(regex: String): Parser[Char] = anyChar().satisfies(_.toString.matches(regex))

  val letter: Parser[Char] = matches("[a-zA-Z]")

  val digit: Parser[Char] = matches("[0-9]")

  val number: Parser[Int] = digit.map(_.asDigit)

  val alphaNum: Parser[Char] = letter <|> digit

  case class string(stringToFind: String) extends Parser[String] {
    override def parseFunction(elementToParse: String) : SuccessParse[String] = {
      throwExceptionIfCondition(
        !elementToParse.startsWith(stringToFind),
        new RuntimeException("El elemento no empieza con el string a buscar")
      )
      (stringToFind, elementToParse.replaceFirst(stringToFind, ""))
    }
  }

}
