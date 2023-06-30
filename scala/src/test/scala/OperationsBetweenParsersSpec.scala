import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

import scala.util.{Failure, Success}

class OperationsBetweenParsersSpec extends AnyFreeSpec {
  "Operaciones entre parsers" - {
    "OR <|>" - {
      val aob = char('a') <|> char('b')
      "Si el string se parsea por A funciona, deberia devolver el resultado de A." in {
        val result = aob.parse("antonio")
        result match {
          case Success(value) => value.parsedValue shouldBe Left('a')
        }
      }
      "Si el string se parsea por B funciona, deberia devolver el resultado de B." in {
        val result = aob.parse("bebe")
        result match {
          case Success(value) => value.parsedValue shouldBe Right('b')
        }
      }
      "Si el string no se parsea ni en A ni en B, deberia de throwear." in {
        val result = aob.parse("tengo")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }

    "CONCAT <>" - {
      val holaMundo = string("hola") <> string("mundo")
      "Si el string se parsea por A, utiliza el resto del texto para parsear en B. Devuelve tupla con los resultados de ambos" in {
        val result = holaMundo.parse("holamundo")
        result match {
          case Success(value) => value.parsedValue shouldBe ("hola", "mundo")
        }
      }
      "Si no logra parsear A, deberia throwear." in {
        val result = holaMundo.parse("mundochau")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
      "Si parsea A pero falla en B, deberia throwear." in {
        val result = holaMundo.parse("holachau")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }

    "RIGHTMOST ~>" - {
      val holaMundo = string("hola") ~> string("mundo")
      "Si el string se parsea por A, utiliza el resto del texto para parsear en B. Devuelve el resultado de B." in {
        val result = holaMundo.parse("holamundo")
        result match {
          case Success(value) => value.parsedValue shouldBe "mundo"
        }
      }
      "Si no logra parsear A, deberia throwear." in {
        val result = holaMundo.parse("mundochau")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
      "Si parsea A pero falla en B, deberia throwear." in {
        val result = holaMundo.parse("holachau")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }

    "LEFTMOST <~" - {
      val holaMundo = string("hola") <~ string("mundo")
      "Si el string se parsea por A, utiliza el resto del texto para parsear en B. Devuelve el resultado de A." in {
        val result = holaMundo.parse("holamundo")
        result match {
          case Success(value) => value.parsedValue shouldBe "hola"
        }
      }
      "Si no logra parsear A, deberia throwear." in {
        val result = holaMundo.parse("mundochau")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
      "Si parsea A pero falla en B, deberia throwear." in {
        val result = holaMundo.parse("holachau")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }
  }
}
