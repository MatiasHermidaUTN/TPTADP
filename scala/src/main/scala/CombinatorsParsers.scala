import scala.util.{Failure, Success, Try}

case class or(firstParser: Parser, secondParser: Parser) extends Parser {
  override def parseo(text: String): Try[successParse] =
    firstParser.parse(text) orElse secondParser.parse(text)
}

case class concat(firstParser: Parser, secondParser: Parser) extends Parser {
  override def parseo(text: String): Try[successParse] = {
    for {
      result1 <- firstParser.parse(text)
      result2 <- secondParser.parse(result1.rest)
    } yield successParse((result1.parsedValue, result2.parsedValue), result2.rest)
  }
}

case class rightMost(firstParser: Parser, secondParser: Parser) extends Parser {
  override def parseo(text: String): Try[successParse] = {
    for {
      result1 <- firstParser.parse(text)
      result2 <- secondParser.parse(result1.rest)
    } yield result2
  }
}

case class leftMost(firstParser: Parser, secondParser: Parser) extends Parser {
  override def parseo(text: String): Try[successParse] = {
    for {
      result1 <- firstParser.parse(text)
      result2 <- secondParser.parse(result1.rest)
    } yield successParse(result1.parsedValue, result2.rest)
  }
}