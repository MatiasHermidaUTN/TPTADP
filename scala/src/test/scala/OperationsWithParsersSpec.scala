import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

import scala.util.{Failure, Success}

class OperationsWithParsersSpec extends AnyFreeSpec {
  "Operaciones con parsers" - {
    "satisfices" - {
      val satisfiesLength = anyChar() satisfies ((parsed: String) => parsed.contains("h"))
      "Si el string se parsea por el parser y cumple la condicion, devuelve el resultado." in {
        val result = satisfiesLength.parse("holaMundo")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe 'h'
            value.rest shouldBe "olaMundo"
          }
        }
      }
      "Si el string se parsea por el parser y no cumple la condicion, throwea." in {
        val result = satisfiesLength.parse("comoEstas")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }
    "opt" - {
      val talVezIn = string("in").opt
      val precedencia = talVezIn <> string("fija")
      "Si el string se parsea por los parsers, devuelve el resultado." in {
        val result = precedencia.parse("infija")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe ("in", "fija")
            value.rest shouldBe ""
          }
        }
      }
      "Si el string no se parsea por el opcional, no afecta al texto y parsea con el segundo, devolviendo el valor esperado." in {
        val result = precedencia.parse("fija")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe ((),"fija")
            value.rest shouldBe ""
          }
        }
      }
    }
    "*" - {
      val charA = char('a').*
      "Si el string se parsea por los parsers, devuelve el resultado." in {
        val result = charA.parse("aaaa")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe List('a', 'a', 'a', 'a')
            value.rest shouldBe ""
          }
        }
      }
      "Si el string no se parsea por el opcional, no afecta al texto y parsea con el segundo, devolviendo el valor esperado." in {
        val result = charA.parse("bbbb")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe List.empty
            value.rest shouldBe "bbbb"
          }
        }
      }
    }
  }
}
