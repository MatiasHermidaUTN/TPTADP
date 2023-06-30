import scala.util.{Failure, Success, Try}

case class or[T, U](firstParser: Parser[T], secondParser: Parser[U]) extends Parser[Either[T, U]] {
  override def parseo(text: String): Try[successParse[Either[T, U]]] = {
    Try {
      firstParser.parse(text) match {
        case Success(value) => successParse(Left(value.parsedValue), value.rest)
        case Failure(_) => secondParser.parse(text) match {
          case Success(value) => successParse(Right(value.parsedValue), value.rest)
          case Failure(_) => throw new RuntimeException("Fallo")
        }
      }
    }
  }
}

case class concat[T, U](firstParser: Parser[T], secondParser: Parser[U]) extends Parser[(T, U)] {
  override def parseo(text: String): Try[successParse[(T, U)]] = {
    for {
      result1 <- firstParser.parse(text)
      result2 <- secondParser.parse(result1.rest)
    } yield successParse((result1.parsedValue, result2.parsedValue), result2.rest)
  }
}

case class rightMost[T, U](firstParser: Parser[T], secondParser: Parser[U]) extends Parser[U] {
  override def parseo(text: String): Try[successParse[U]] = {
    Try {
      val result = concat(firstParser, secondParser).parse(text).get
      successParse(result.parsedValue._2, result.rest)
    }
  }
}

case class leftMost[T, U](firstParser: Parser[T], secondParser: Parser[U]) extends Parser[T] {
  override def parseo(text: String): Try[successParse[T]] = {
    Try {
      val result = concat(firstParser, secondParser).parse(text).get
      successParse(result.parsedValue._1, result.rest)
    }
  }
}