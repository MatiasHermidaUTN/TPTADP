import ExceptionsHelper.throwExceptionIfCondition
import Parser.{Parser, SuccessParse}
import scala.util.{Failure, Success, Try}

object OperationsParsers {
  case class satisfy[T](parser: Parser[T], condition: T => Boolean) extends Parser[T] {
    override def parseFunction(elementToParse: String): SuccessParse[T] = {
      val (value, rest) = parser.parseFunction(elementToParse)
      throwExceptionIfCondition(
        !condition(value),
        new RuntimeException("El elemento parseado no satisface la condicion dada")
      )
      (value, rest)
    }
  }

  case class optional[T](parser: Parser[T]) extends Parser[Option[T]] {

    override def parse(elementToParse: String): Try[(Option[T], String)] = {
      Try {
        parseFunction(elementToParse)
      }
    }

    override def parseFunction(elementToParse: String): SuccessParse[Option[T]] = {
      val result = parser.parse(elementToParse)
      result match {
        case Success((value, rest)) => (Some(value), rest)
        case Failure(_) => (None, elementToParse)
      }
    }
  }

  case class kleene[T](parser: Parser[T]) extends Parser[List[T]] {

    private def getParseIterations(elementToParse: String, iterationsValues: List[T]): SuccessParse[List[T]] = {
      val parsedElement = parser.parse(elementToParse)
      parsedElement match {
        case Failure(_) =>
          val result = (iterationsValues, elementToParse)
          result
        case Success((value, rest)) =>
          getParseIterations(rest, iterationsValues ::: List(value))
      }
    }

    override def parse(elementToParse: String): Try[(List[T], String)] = {
      Try {
        parseFunction(elementToParse)
      }
    }

    override def parseFunction(elementToParse: String): SuccessParse[List[T]] = {
      getParseIterations(elementToParse, List.empty)
    }
  }

  def plus[T](parser: Parser[T]): Parser[List[T]] = parser.*().satisfies(_.nonEmpty)

  def separator[T, U](parser: Parser[T], sepParser: Parser[U]): Parser[List[T]] = ((parser <~ sepParser).+ <> parser).map {
    case (valueList, lastValue) => valueList ::: List(lastValue)
  }

  def constant[T, U](parser: Parser[T], value: U): Parser[U] = parser.map{ _ => value }

  case class mapped[T, U](parser: Parser[T], transform: T => U) extends Parser[U] {
    override def parseFunction(elementToParse: String): (U, String) = {
      val (value, rest) = parser.parseFunction(elementToParse)
      (transform(value), rest)
    }
  }

}
