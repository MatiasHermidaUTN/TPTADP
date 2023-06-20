import scala.util.{Failure, Success, Try}

case class successParse(parsedValue: Any, rest: String)

case class anyChar() extends Parser {
  override def parseo(text: String): Try[successParse] =
    Try (successParse(text(0), text.substring(1)))
}

case class char(char: Char) extends Parser {
  override def parseo(text: String): Try[successParse] = {
    Try {
      if (!text.startsWith(char.toString())) {
        throw new RuntimeException("Fallo")
      }
      successParse(char, text.substring(1))
    }
  }
}

case class void() extends Parser {
  override def parseo(text: String): Try[successParse] =
    Try(successParse((), text.substring(1)))
}

case class letter() extends Parser {
  override def parseo(text: String): Try[successParse] = {
    Try {
      val char = text(0)
      if (!char.toString().matches("[a-zA-Z]")) {
        throw new RuntimeException("Fallo")
      }
      successParse(char, text.substring(1))
    }
  }
}

case class digit() extends Parser {
  override def parseo(text: String): Try[successParse] = {
    Try {
      val char = text(0)
      if (!char.toString().matches("\\d")) {
        throw new RuntimeException("Fallo")
      }
      successParse(char, text.substring(1))
    }
  }
}

case class alphNum() extends Parser {
  override def parseo(text: String): Try[successParse] =
    letter().parse(text) orElse digit().parse(text)
}

case class string(string: String) extends Parser {
  override def parseo(text: String): Try[successParse] = {
    Try {
      if (!text.startsWith(string)) {
        throw new RuntimeException("Fallo")
      }
      successParse(string, text.substring(string.length))
    }
  }
}

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

abstract class Parser {
  var optional = false
  def opt(): Parser = {
    optional = true
    this
  };

  def parse(text: String): Try[successParse] = {
    Try {
      val trimmedText = text.trim
      if ("".equals(trimmedText)) {
        throw new RuntimeException("Fallo")
      }
      val parseResult = parseo(trimmedText)
      if (!optional)
        return parseResult
      parseResult match {
        case Success(successResult) => successResult
        case Failure(_) => successParse((), text)
      }
    }
  }
  def parseo(text: String): Try[successParse]
  def <|>(otherParser: Parser): Parser = or(this, otherParser)

  def <>(otherParser: Parser): Parser = concat(this, otherParser)

  def ~>(otherParser: Parser): Parser = rightMost(this, otherParser)

  def <~(otherParser: Parser): Parser = leftMost(this, otherParser)

  def satisfies(condition: String => Boolean) = satisfiesParser(this, condition)

  def *(): Parser = {
    val parser = this
    new Parser {
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
  }
}