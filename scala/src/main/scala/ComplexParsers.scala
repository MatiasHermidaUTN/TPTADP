import scala.util.{Failure, Success, Try}

case class satisfiesParser(firstParser: Parser, condition: String => Boolean) extends Parser {
  override def parseo(text: String): Try[successParse] = {
    Try {
      val result = firstParser.parse(text).get
      if (!condition.apply(result.parsedValue.toString())) {
        throw new RuntimeException("Fallo")
      }
      result
    }
  }
}

case class optionalParser(parser: Parser) extends Parser {
  override def parseo(text: String): Try[successParse] = parser.parse(text)
  override def returnParseo(result: Try[successParse], text: String): Try[successParse] = {
    Try {
      result match {
        case Success(successResult) => successResult
        case Failure(_) => successParse((), text)
      }
    }
  }
}

case class kleeneParser(parser: Parser) extends Parser {
  override def parseo(text: String): Try[successParse] = {
    Try {
      var results: List[Any] = List.empty
      var rest: String = text
      var parseResult = parser.parse(text)
      while (parseResult.isSuccess) {
        val result = parseResult.get
        rest = result.rest
        results = results ::: List(result.parsedValue)
        parseResult = parser.parse(rest)
      }
      successParse(results, rest)
    }
  }
}
