import org.scalatest.matchers.should.Matchers._
import org.scalatest.freespec.AnyFreeSpec

import scala.util.{Failure, Success}

class ParserSpec extends AnyFreeSpec {
  "Parsers Basicos" - {
    "AnyCharParser" - {
      val anyCharParser = anyChar
      "Si el string tiene caracteres, deberia devolver el primero de todos." in {
        val result = anyCharParser.parse("tadp")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe 't'
            value.rest shouldBe "adp"
          }
        }
      }
      "Si el string no tiene caracteres, deberia de throwear." in {
        val result = anyCharParser.parse("")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }

    "CharParser" - {
      val charParser = char('r')
      "Si el string contiene el char, debe devolverlo." in {
        val result = charParser.parse("rosario")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe 'r'
            value.rest shouldBe "osario"
          }
        }
      }

      "Si el string contiene el char, pero no es en el primer caracter, debe throwear." in {
        val result = charParser.parse("armario")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
      "Si el string solo tiene espacios, deberia de throwear." in {
        val result = charParser.parse("como")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }

    "VoidParser" - {
      val voidParser = void
      "Si el string contiene un caracter, devuelve Unit." in {
        val result = voidParser.parse("r")
        result match {
          case Success(value) => {
            value.parsedValue shouldEqual ()
            value.rest shouldEqual ""
          }
        }
      }
      "Si el string contiene varios caracteres, devuelve Unit" in {
        val result = voidParser.parse("armario")
        result match {
          case Success(value) => {
            value.parsedValue shouldEqual()
            value.rest shouldEqual "rmario"
          }
        }
      }
      "Si el string no tiene caracteres, debe throwear." in {
        val result = voidParser.parse("")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }

    "LetterParser" - {
      val letterParser = letter
      "Si el string contiene solo letras, devuelve el primer caracter." in {
        val result = letterParser.parse("hola")
        result match {
          case Success(value) => {
            value.parsedValue shouldEqual 'h'
            value.rest shouldEqual "ola"
          }
        }
      }
      "Si el string empieza con letra, devuelve el primer caracter." in {
        val result = letterParser.parse("arm2ario")
        result match {
          case Success(value) => {
            value.parsedValue shouldEqual 'a'
            value.rest shouldEqual "rm2ario"
          }
        }
      }
      "Si el string empieza con numero, throwea." in {
        val result = letterParser.parse("2holas")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
      "Si el string contiene numeros, throwea." in {
        val result = letterParser.parse("123131")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }
  }

  "DigitParser" - {
    val digitParser = digit
    "Si el string contiene solo numeros, devuelve el primer caracter." in {
      val result = digitParser.parse("1234")
      result match {
        case Success(value) => {
          value.parsedValue shouldEqual '1'
          value.rest shouldEqual "234"
        }
      }
    }
    "Si el string empieza con numero, devuelve el primer caracter." in {
      val result = digitParser.parse("2holas")
      result match {
        case Success(value) => {
          value.parsedValue shouldEqual '2'
          value.rest shouldEqual "holas"
        }
      }
    }
    "Si el string empieza con letra, throwea." in {
      val result = digitParser.parse("arm2ario")
      result match {
        case Failure(exception) => exception.getMessage shouldBe "Fallo"
      }
    }
    "Si el string contiene letras unicamente, throwea." in {
      val result = digitParser.parse("hola")
      result match {
        case Failure(exception) => exception.getMessage shouldBe "Fallo"
      }
    }
  }

  "AlphNumParser" - {
    val alphNumParser = alphNum
    "Si el string contiene solo numeros, devuelve el primer caracter." in {
      val result = alphNumParser.parse("1234")
      result match {
        case Success(value) => {
          value.parsedValue shouldEqual '1'
          value.rest shouldEqual "234"
        }
      }
    }
    "Si el string contiene letras unicamente, devuelve el primer caracter." in {
      val result = alphNumParser.parse("hola")
      result match {
        case Success(value) => {
          value.parsedValue shouldEqual 'h'
          value.rest shouldEqual "ola"
        }
      }
    }
    "Si el string contiene letras y numeros, devuelve el primer caracter." in {
      val result = alphNumParser.parse("arm2ario")
      result match {
        case Success(value) => {
          value.parsedValue shouldEqual 'a'
          value.rest shouldEqual "rm2ario"
        }
      }
    }
  }

  "StringParser" - {
    val stringParser = string("hola")
    "Si el string empieza con el string esperado, devuelve el string parseado." in {
      val result = stringParser.parse("hola mundo!")
      result match {
        case Success(value) => {
          value.parsedValue shouldEqual "hola"
          value.rest shouldEqual " mundo!"
        }
      }
    }
    "Si el string contiene en el medio el string esperado, throwea." in {
      val result = stringParser.parse("Buenas! hola!")
      result match {
        case Failure(exception) => exception.getMessage shouldBe "Fallo"
      }
    }
    "El parser es caseSensitive, va a throwear si no cumple." in {
      val result = stringParser.parse("Hola")
      result match {
        case Failure(exception) => exception.getMessage shouldBe "Fallo"
      }
    }
    "Si el string no contiene el string esperado, throwea." in {
      val result = stringParser.parse("arm2ario")
      result match {
        case Failure(exception) => exception.getMessage shouldBe "Fallo"
      }
    }
  }
}
