import ExceptionsHelper.throwExceptionIfCondition
import Parser.{Parser, SuccessParse}

import scala.util.{Try, Failure, Success}

object OperationsParsers {
  case class satisfy[T](parser: Parser[T], condition: T => Boolean) extends Parser[T] {
    override def parseFunction(elementToParse: String): SuccessParse[T] = {
      val result = parser.parseFunction(elementToParse)
      throwExceptionIfCondition(
        !condition(result._1),
        new RuntimeException("El elemento parseado no satisface la condicion dada")
      )
      result
    }
  }

  case class optional[T](parser: Parser[T]) extends Parser[Option[T]] {
    override def parseFunction(elementToParse: String): SuccessParse[Option[T]] = {
      val result = Try { parser.parseFunction(elementToParse) }
      result match {
        case Success(value) => (Some(value._1), value._2)
        case Failure(_) => (None, elementToParse)
      }
    }
  }

  case class kleene[T](parser: Parser[T]) extends Parser[List[T]] {

    var parsedList : List[T] = List.empty

    override def parseFunction(elementToParse: String): (List[T], String) = {
      val parsedElement = parser.parse(elementToParse)
      parsedElement match {
        case Failure(_) =>
          val result = (parsedList, elementToParse)
          parsedList = List.empty
          result
        case Success(value) =>
          parsedList = parsedList ::: List(value._1)
          parseFunction(value._2)
      }
    }
  }

  case class plus[T](parser: Parser[T]) extends Parser[List[T]] {

    var parsedList : List[T] = List.empty

    override def parseFunction(elementToParse: String): (List[T], String) = {
      val parsedElement = parser.parse(elementToParse)
      parsedElement match {
        case Failure(exception) if parsedList.isEmpty =>
          throw exception
        case Failure(_) =>
          val result = (parsedList, elementToParse)
          parsedList = List.empty
          result
        case Success(value) =>
          parsedList = parsedList ::: List(value._1)
          parseFunction(value._2)
      }
    }
  }

  case class separator[T, U](parser: Parser[T], sepParser: Parser[U]) extends Parser[List[T]] {
    override def parseFunction(elementToParse: String): (List[T], String) = {
      ???
    }
  }

}
