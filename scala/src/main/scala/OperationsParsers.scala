import ExceptionsHelper.throwExceptionIfCondition
import Parser.{Parser, SuccessParse}

import scala.util.{Try, Failure, Success}

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

    var parsedList : List[T] = List.empty

    override def parse(elementToParse: String): Try[(List[T], String)] = {
      Try {
        parseFunction(elementToParse)
      }
    }

    override def parseFunction(elementToParse: String): SuccessParse[List[T]] = {
      val parsedElement = parser.parse(elementToParse)
      parsedElement match {
        case Failure(_) =>
          val result = (parsedList, elementToParse)
          parsedList = List.empty
          result
        case Success((value, rest)) =>
          parsedList = parsedList ::: List(value)
          parseFunction(rest)
      }
    }
  }

  case class plus[T](parser: Parser[T]) extends Parser[List[T]] {
    override def parseFunction(elementToParse: String): SuccessParse[List[T]] = {
      val (value, rest) = parser.*.parseFunction(elementToParse)
      if(value.isEmpty) throw new RuntimeException("No se encontraron 1 o mas casos")
      (value, rest)
    }
  }

  case class separator[T, U](parser: Parser[T], sepParser: Parser[U]) extends Parser[List[T]] {

    var parsedList : List[T] = List.empty

    override def parseFunction(elementToParse: String): SuccessParse[List[T]] = {
      val (value, rest) = parser.parseFunction(elementToParse)
      parsedList = parsedList ::: List(value)
      if(rest.isEmpty) {
        if(parsedList.length == 1)
          throw new RuntimeException("No se encontraron 1 o mas casos")
        val result = (parsedList, "")
        parsedList = List.empty
        return result
      }
      val (_, sepRest) = sepParser.parseFunction(rest)
      parseFunction(sepRest)
    }

  }

  case class constant[T, U](parser: Parser[T], value: U) extends Parser[U] {
    override def parseFunction(elementToParse: String): SuccessParse[U] = {
      val (_, rest) = parser.parseFunction(elementToParse)
      (value, rest)
    }
  }

  case class mapped[T, U](parser: Parser[T], transform: T => U) extends Parser[U] {
    override def parseFunction(elementToParse: String): (U, String) = {
      val (value, rest) = parser.parseFunction(elementToParse)
      (transform(value), rest)
    }
  }

}
