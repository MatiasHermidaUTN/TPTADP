import Parser.{Parser, SuccessParse}
import ParsersHelper.{orParsers, concatParsers}

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

  case class concat[T](aParser: Parser[T], anotherParser: Parser[T]) extends Parser[(T,T)] {
    override def parseFunction(elementToParse: String): SuccessParse[(T,T)] = {
      concatParsers(
        elementToParse,
        aParser,
        anotherParser,
        (result1: SuccessParse[T], result2: SuccessParse[T]) => ((result1._1, result2._1), result2._2)
      )
    }
  }

  case class rightMost[T](aParser: Parser[T], anotherParser: Parser[T]) extends Parser[T] {
    override def parseFunction(elementToParse: String): SuccessParse[T] = {
      concatParsers(
        elementToParse,
        aParser,
        anotherParser,
        (_: SuccessParse[T], result2: SuccessParse[T]) => (result2._1, result2._2)
      )
    }
  }

  case class leftMost[T](aParser: Parser[T], anotherParser: Parser[T]) extends Parser[T] {
    override def parseFunction(elementToParse: String): SuccessParse[T] = {
      concatParsers(
        elementToParse,
        aParser,
        anotherParser,
        (result1: SuccessParse[T], result2: SuccessParse[T]) => (result1._1, result2._2)
      )
    }
  }

}
