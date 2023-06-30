import scala.util.{Try}

case object anyChar extends Parser[Char] {
  override def parseo(text: String): Try[successParse[Char]] =
    Try(successParse(text(0), text.substring(1)))
}

case class char(char: Char) extends Parser[Char] {
  override def parseo(text: String): Try[successParse[Char]] = {
    Try {
      if (!text.startsWith(char.toString())) {
        throw new RuntimeException("Fallo")
      }
      successParse(char, text.substring(1))
    }
  }
}

case object void extends Parser[Unit] {
  override def parseo(text: String): Try[successParse[Unit]] =
    Try(successParse((), text.substring(1)))
}

case object letter extends Parser[Char]  {
  override def parseo(text: String): Try[successParse[Char]] = {
    Try {
      val char = text(0)
      if (!char.toString().matches("[a-zA-Z]")) {
        throw new RuntimeException("Fallo")
      }
      successParse(char, text.substring(1))
    }
  }
}

case object digit extends Parser[Char] {
  override def parseo(text: String): Try[successParse[Char]] = {
    Try {
      val char = text(0)
      if (!char.toString().matches("\\d")) {
        throw new RuntimeException("Fallo")
      }
      successParse(char, text.substring(1))
    }
  }
}

case object alphNum extends Parser[Char] {
  override def parseo(text: String): Try[successParse[Char]] =
    letter.parse(text) orElse digit.parse(text)
}

case class string(string: String) extends Parser[String] {
  override def parseo(text: String): Try[successParse[String]] = {
    Try {
      if (!text.startsWith(string)) {
        throw new RuntimeException("Fallo")
      }
      successParse(string, text.substring(string.length))
    }
  }
}

case object integer extends Parser[Int] {
  override def parseo(text: String): Try[successParse[Int]] = {
    Try {
      val result = digit.+.parse(text).get
      successParse(result.parsedValue.mkString("").toInt, result.rest)
    }
  }
}