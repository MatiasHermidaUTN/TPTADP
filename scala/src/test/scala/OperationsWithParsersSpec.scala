import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

import scala.util.{Failure, Success}

class OperationsWithParsersSpec extends AnyFreeSpec {
  "Operaciones con parsers" - {
    "satisfices" - {
      val satisfiesLength = anyChar satisfies ((parsed: String) => parsed.contains("h"))
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
            value.parsedValue shouldBe (Left("in"), "fija")
            value.rest shouldBe ""
          }
        }
      }
      "Si el string no se parsea por el opcional, no afecta al texto y parsea con el segundo, devolviendo el valor esperado." in {
        val result = precedencia.parse("fija")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe (Right(()),"fija")
            value.rest shouldBe ""
          }
        }
      }
    }
    "*" - {
      val charA = char('a').*
      "Si el string se parsea por los parsers, devuelve todos los parseos posibles." in {
        val result = charA.parse("aaaa")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe List('a', 'a', 'a', 'a')
            value.rest shouldBe ""
          }
        }
      }
      "Si el string falla, para de parsear y devuelve todos los parseos posibles." in {
        val result = alphNum.*.parse("1asd2 a21e")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe List('1', 'a', 's', 'd', '2')
            value.rest shouldBe " a21e"
          }
        }
      }
      "Si el string no se parsea por el parser, devuelve una lista vacia." in {
        val result = charA.parse("bbbb")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe List.empty
            value.rest shouldBe "bbbb"
          }
        }
      }
    }

    "+" - {
      val charA = char('a').+
      "Si el string se parsea por los parsers, devuelve todos los parseos posibles." in {
        val result = charA.parse("aaaa")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe List('a', 'a', 'a', 'a')
            value.rest shouldBe ""
          }
        }
      }
      "Si el string no se parsea por el parser, devuelve una lista vacia." in {
        val result = charA.parse("bbbb")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }

    "sepBy" - {
      "Con telefono de digit" - {
        val numeroDeTelefono = digit.sepBy(char('-'))
        "Si el string se parsea por el parser, despues por el separador y vuelve a parsear por el parser, devuelve todos los parseos sin el del separador." in {
          val result = numeroDeTelefono.parse("4356-1234")
          result match {
            case Success(value) => {
              val grupos: List[List[Char]] = List(List('4', '3', '5', '6'), List('1', '2', '3', '4'))
              value.parsedValue shouldBe grupos
              value.rest shouldBe ""
            }
          }
        }
        "Si el string no se parsea mas de una vez por el contenedor, falla." in {
          val result = numeroDeTelefono.parse("j-1234")
          result match {
            case Failure(exception) => exception.getMessage shouldBe "Fallo"
          }
        }
        "Si el string se parsea por el parser pero no por el separador, falla." in {
          val result = numeroDeTelefono.parse("4356")
          result match {
            case Failure(exception) => exception.getMessage shouldBe "Fallo"
          }
        }
        "Si el string no se parsea por el parser separador, falla." in {
          val result = numeroDeTelefono.parse("4356 1234")
          result match {
            case Failure(exception) => exception.getMessage shouldBe "Fallo"
          }
        }
      }
      "Con telefono por integer" - {
        val numeroDeTelefono = integer.sepBy(char('-'))
        "Si el string se parsea por el parser, despues por el separador y vuelve a parsear por el parser, devuelve todos los parseos sin el del separador." in {
          val result = numeroDeTelefono.parse("4356-1234")
          result match {
            case Success(value) => {
              val grupos: List[List[Int]] = List(List(4356), List(1234))
              value.parsedValue shouldBe grupos
              value.rest shouldBe ""
            }
          }
        }
        "Si el string no se parsea mas de una vez por el contenedor, falla." in {
          val result = numeroDeTelefono.parse("j-1234")
          result match {
            case Failure(exception) => exception.getMessage shouldBe "Fallo"
          }
        }
        "Si el string se parsea por el parser pero no por el separador, falla." in {
          val result = numeroDeTelefono.parse("4356")
          result match {
            case Failure(exception) => exception.getMessage shouldBe "Fallo"
          }
        }
        "Si el string no se parsea por el parser separador, falla." in {
          val result = numeroDeTelefono.parse("4356 1234")
          result match {
            case Failure(exception) => exception.getMessage shouldBe "Fallo"
          }
        }
      }
    }

    "const" - {
      val trueParser = string("true").const(true)
      "Si el string se parsea por el parser, devuelve el valor constante." in {
        val result = trueParser.parse("true")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe true
            value.rest shouldBe ""
          }
        }
      }
      "Si el string no se parsea por el parser, throwea." in {
        val result = trueParser.parse("false")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }

    "map" - {
      case class Persona(nombre: String, apellido: String)
      val personaParser = (alphNum.* <> (char(' ') ~> alphNum.*))
        .map { case (nombre, apellido) => Persona(nombre.mkString(""), apellido.mkString("")) }
      "Si el string se parsea por el parser, devuelve el valor mapeado." in {
        val result = personaParser.parse("juan pepe")
        result match {
          case Success(value) => {
            value.parsedValue shouldBe Persona("juan", "pepe")
            value.rest shouldBe ""
          }
        }
      }
      "Si el string no se parsea por el parser, throwea." in {
        val result = personaParser.parse("false")
        result match {
          case Failure(exception) => exception.getMessage shouldBe "Fallo"
        }
      }
    }
  }
}
