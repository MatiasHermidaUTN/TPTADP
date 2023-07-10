import Parser.{Parser, SuccessParse}
import ParsersHelper.{expandTry}

object CombinableParsers {
  case class or[T](aParser: Parser[T], anotherParser: Parser[T]) extends Parser[T] {
    override def parseFunction(elementToParse: String): SuccessParse[T] = {
      val result = aParser.parse(elementToParse) orElse anotherParser.parse(elementToParse)
      expandTry(result, new RuntimeException("El elemento no satisface ningun parser"))
    }
  }

  case class concat[T, U](aParser: Parser[T], anotherParser: Parser[U]) extends Parser[(T,U)] {
    override def parseFunction(elementToParse: String): SuccessParse[(T,U)] = {
      val result = for {
        (value1, rest1) <- aParser.parse(elementToParse)
        (value2, rest2) <- anotherParser.parse(rest1)
      } yield ((value1, value2), rest2)
      expandTry(result, new RuntimeException(s"El elemento no satisface el parser concatenado"))
    }
  }

  def rightMost[T, U](aParser: Parser[T], anotherParser: Parser[U]): Parser[U] = (aParser <> anotherParser).map {
    case (_, value2) => value2
  }

  def leftMost[T, U](aParser: Parser[T], anotherParser: Parser[U]): Parser[T] = (aParser <> anotherParser).map {
    case (value1, _) => value1
  }

}
