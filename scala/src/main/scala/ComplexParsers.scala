import scala.util.{Failure, Success, Try}

case class satisfiesParser[T](firstParser: Parser[T], condition: String => Boolean) extends Parser[T] {
  override def parseo(text: String): Try[successParse[T]] = {
    Try {
      val result = firstParser.parse(text).get
      if (!condition.apply(result.parsedValue.toString())) {
        throw new RuntimeException("Fallo")
      }
      result
    }
  }
}

case class optionalParser[T](parser: Parser[T]) extends Parser[Either[T, Unit]] {
  override def parseo(text: String): Try[successParse[Either[T, Unit]]] = {
    Try {
      parser.parse(text) match {
        case Success(successResult) => successParse(Left(successResult.parsedValue), successResult.rest)
        case Failure(_) => successParse(Right(()), text)
      }
    }
  }
}

case class kleeneParser[T](parser: Parser[T]) extends Parser[List[T]] {
  override def parseo(text: String): Try[successParse[List[T]]] = {
    Try {
      var results: List[T] = List.empty
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

case class strictKleeneParser[T](kleene: Parser[List[T]]) extends Parser[List[T]] {
  override def parseo(text: String): Try[successParse[List[T]]] = {
    Try {
      val resultParse = kleene.parse(text).get
      if (resultParse.parsedValue.length == 0) {
        throw new RuntimeException("Fallo")
      }
      resultParse
    }
  }
}

case class sepByParser[T, U](contenido: Parser[T], separador: Parser[U]) extends Parser[List[List[T]]] {
  override def parseo(text: String): Try[successParse[List[List[T]]]] = {
    Try {
      val parser = contenido.+
      val responseParser = parser.parse(text).get
      val parserConSeparador = (separador ~> parser).+
      val resultadoParserConSeparador = parserConSeparador.parse(responseParser.rest).get
      successParse(List(responseParser.parsedValue) ::: resultadoParserConSeparador.parsedValue, resultadoParserConSeparador.rest)
    }
  }
}

case class constParser[U, T](parser: Parser[T], constant: U) extends Parser[U] {
  override def parseo(text: String): Try[successParse[U]] = {
    Try {
      val result = parser.parse(text).get
      successParse(constant, result.rest)
    }
  }
}

case class mapParser[U, T](parser: Parser[T], mapper: T => U) extends Parser[U] {
  override def parseo(text: String): Try[successParse[U]] = {
    Try {
      val result = parser.parse(text).get
      successParse(mapper.apply(result.parsedValue), result.rest)
    }
  }
}