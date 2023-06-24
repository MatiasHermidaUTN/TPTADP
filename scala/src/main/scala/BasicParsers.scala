import scala.util.{Failure, Success, Try}

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