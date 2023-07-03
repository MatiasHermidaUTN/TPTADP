import Parser.{Parser, SuccessParse}
import ParsersHelper.{expandTry, orParsers}

object CombinableParsers {
  case class or[T](aParser: Parser[T], anotherParser: Parser[T]) extends Parser[T] {
    override def parseFunction(elementToParse: String): SuccessParse[T] = {
      orParsers(
        elementToParse,
        aParser,
        anotherParser,
        new RuntimeException("El elemento no satisface ningun parser")
      )
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

  case class rightMost[T, U](aParser: Parser[T], anotherParser: Parser[U]) extends Parser[U] {
    override def parseFunction(elementToParse: String): SuccessParse[U] = {
      val result = for {
        (_, rest1) <- aParser.parse(elementToParse)
        (value2, rest2) <- anotherParser.parse(rest1)
      } yield (value2, rest2)
      expandTry(result, new RuntimeException("El elemento no satisface el parser concatenado"))
    }
  }

  case class leftMost[T, U](aParser: Parser[T], anotherParser: Parser[U]) extends Parser[T] {
    override def parseFunction(elementToParse: String): SuccessParse[T] = {
      val result = for {
        (value1, rest1) <- aParser.parse(elementToParse)
        (_, rest2) <- anotherParser.parse(rest1)
      } yield (value1, rest2)
      expandTry(result, new RuntimeException(s"$elementToParse El elemento no satisface el parser concatenado"))
    }
  }

}
