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
        result1 <- aParser.parse(elementToParse)
        result2 <- anotherParser.parse(result1._2)
      } yield ((result1._1, result2._1), result2._2)
      expandTry(result, new RuntimeException("El elemento no satisface el parser concatenado"))
    }
  }

  case class rightMost[T, U](aParser: Parser[T], anotherParser: Parser[U]) extends Parser[U] {
    override def parseFunction(elementToParse: String): SuccessParse[U] = {
      val result = for {
        result1 <- aParser.parse(elementToParse)
        result2 <- anotherParser.parse(result1._2)
      } yield (result2._1, result2._2)
      expandTry(result, new RuntimeException("El elemento no satisface el parser concatenado"))
    }
  }

  case class leftMost[T, U](aParser: Parser[T], anotherParser: Parser[U]) extends Parser[T] {
    override def parseFunction(elementToParse: String): SuccessParse[T] = {
      val result = for {
        result1 <- aParser.parse(elementToParse)
        result2 <- anotherParser.parse(result1._2)
      } yield (result1._1, result2._2)
      expandTry(result, new RuntimeException("El elemento no satisface el parser concatenado"))
    }
  }

}
