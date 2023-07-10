import BasicParsers.{alphaNum, anyChar, char, digit, letter, number, string, void}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.freespec.AnyFreeSpec

import scala.util.{Failure, Success}

class ProjectSpec extends AnyFreeSpec {
  "Parsers Basicos" - {
    "anyChar Parser" - {
      val anyChar = new anyChar()
      "recibe el string hola, retorna Success con char h" in {
        val Success(result) = anyChar.parse("hola")
        result shouldBe ('h', "ola")
      }
      "recibe un string vacio, retorna Failure" in {
        val Failure(exception) = anyChar.parse("")
        exception.getMessage shouldBe "No se encontro ningun caracter"
      }
    }
    "char Parser" - {
      "recibe el string chau, retorna Success con char c" in {
        val Success(result) = char('c').parse("chau")
        result shouldBe ('c', "hau")
      }
      "recibe el string hola, retorna Failure" in {
        val Failure(exception) = char('c').parse("hola")
        exception.getMessage shouldBe "El elemento parseado no satisface la condicion dada"
      }
    }
    "void Parser" - {
      "recibe el string hola, retorna Success con Unit" in {
        val Success(result) = void.parse("hola")
        result shouldBe ((), "ola")
      }
    }
    "letter Parser" - {
      "recibe el string hola, retorna Success con char h" in {
        val Success(result) = letter.parse("hola")
        result shouldBe ('h', "ola")
      }
      "recibe el string 1234, retorna Failure" in {
        val Failure(exception) = letter.parse("1234")
        exception.getMessage shouldBe "El elemento parseado no satisface la condicion dada"
      }
    }
    "digit Parser" - {
      "recibe el string 1234, retorna Success con char 1" in {
        val Success(result) = digit.parse("1234")
        result shouldBe ('1', "234")
      }
      "recibe el string hola, retorna Failure" in {
        val Failure(exception) = digit.parse("hola")
        exception.getMessage shouldBe "El elemento parseado no satisface la condicion dada"
      }
    }
    "alphaNum Parser" - {

      "recibe el string 1234, retorna Success con char 1" in {
        val Success(result) = alphaNum.parse("1234")
        result shouldBe ('1', "234")
      }
      "recibe el string hola, retorna Success con char h" in {
        val Success(result) = alphaNum.parse("hola")
        result shouldBe ('h', "ola")
      }
      "recibe el string *hola, retorna Failure" in {
        val Failure(exception) = alphaNum.parse("*hola")
        exception.getMessage shouldBe "El elemento no satisface ningun parser"
      }
    }
    "string Parse" - {
      val string = new string("hola")
      "recibe el string hola mundo! buscando hola, retorna Success con hola" in {
        val Success(result) = string.parse("hola mundo!")
        result shouldBe ("hola", " mundo!")
      }
    }

  }
  "Parsers Combinados" - {
    "OR Combinator" - {
      "combina parsers char('a') y char('b') con <|> y recibe arbol, retorna Success con a" in {
        val aob = char('a') <|> char('b')
        val Success(result) = aob.parse("arbol")
        result shouldBe ('a', "rbol")
      }
      "combina parsers char('a') y char('b') con <|> y recibe burt, retorna Success con b" in {
        val aob = char('a') <|> char('b')
        val Success(result) = aob.parse("burt")
        result shouldBe ('b', "urt")
      }
      "combina parsers char('a') y char('b') con <|> y recibe holamundo, retorna Failure" in {
        val aob = char('a') <|> char('b')
        val Failure(exception) = aob.parse("holamundo")
        exception.getMessage shouldBe "El elemento no satisface ningun parser"
      }
    }
    "Concat Combinator" - {
      "combina parsers string(hola) y string(mundo) con <> y recibe holamundo, retorna Success con (hola, mundo)" in {
        val holamundo = string("hola") <> string("mundo")
        val Success(result) = holamundo.parse("holamundo")
        result shouldBe (("hola", "mundo"), "")
      }
      "combina parsers string(hola) y string(mundo) con <> y recibe holachau, retorna Failure" in {
        val holamundo = string("hola") <> string("mundo")
        val Failure(exception) = holamundo.parse("holachau")
        exception.getMessage shouldBe "El elemento no satisface el parser concatenado"
      }
    }
    "RightMost Combinator" - {
      "combina parsers string(hola) y string(mundo) con ~> y recibe holamundo, retorna Success con mundo" in {
        val holamundo = string("hola") ~> string("mundo")
        val Success(result) = holamundo.parse("holamundo")
        result shouldBe ("mundo", "")
      }
      "combina parsers string(hola) y string(mundo) con <> y recibe holachau, retorna Failure" in {
        val holamundo = string("hola") ~> string("mundo")
        val Failure(exception) = holamundo.parse("holachau")
        exception.getMessage shouldBe "El elemento no satisface el parser concatenado"
      }
    }
    "LeftMost Combinator" - {
      "combina parsers string(hola) y string(mundo) con ~> y recibe holamundo, retorna Success con hola" in {
        val holamundo = string("hola") <~ string("mundo")
        val Success(result) = holamundo.parse("holamundo")
        result shouldBe ("hola", "")
      }
      "combina parsers string(hola) y string(mundo) con <> y recibe holachau, retorna Failure" in {
        val holamundo = string("hola") <~ string("mundo")
        val Failure(exception) = holamundo.parse("holachau")
        exception.getMessage shouldBe "El elemento no satisface el parser concatenado"
      }
    }
  }
  "Parsers Operations" - {
    "satisfies" - {
      "dado un parser string(hola) que satisface si el elemento parseado tiene mas de 3 caracteres y recibe holamundo, retorna Success con hola" in {
        val holaParser = string("hola").satisfies(_.length > 3)
        val Success(result) = holaParser.parse("holamundo")
        result shouldBe ("hola", "mundo")
      }
      "dado un parser string(hola) que satisface si el elemento parseado tiene mas de 3 caracteres y recibe holamundo, retorna Failure" in {
        val holaParser = string("hol").satisfies(_.length > 3)
        val Failure(exception) = holaParser.parse("holamundo")
        exception.getMessage shouldBe "El elemento parseado no satisface la condicion dada"
      }
    }
    "opt" - {
      "dado un parser opcional string(in), concatenado con un parser string(fija) que recibe fija, retorna Success con None y resto fija" in {
        val talVezIn = string("in").opt
        val precedencia = talVezIn <> string("fija")
        val Success(result) = precedencia.parse("fija")
        result shouldBe ((None, "fija"), "")
      }
    }
    "kleene" - {
      val kleeneParser = char('c').*
      "dado un parser char('c') de kleene que recibe cccasa, retorna Success con (List('c','c','c'), asa)" in {
        val Success(result) =kleeneParser.parse("cccasa")
        result shouldBe (List('c','c','c'), "asa")
      }
      "dado un parser char('c') de kleene que recibe burt, retorna Success con (List.empty, burt)" in {
        val Success(result) = kleeneParser.parse("burt")
        result shouldBe (List.empty, "burt")
      }
    }
    "plus" - {
      val charPlusParser = char('c').+
      val stringPlusParser = string("cha").+
      "dado un parser char('c') de kleene+ que recibe cccasa, retorna Success con (List('c','c','c'), asa)" in {
        val Success(result) = charPlusParser.parse("cccasa")
        result shouldBe (List('c','c','c'), "asa")
      }
      "dado un parser string('cha') de kleene+ que recibe chachachan, retorna Success con (List(cha,cha,cha), n)" in {
        val Success(result) = stringPlusParser.parse("chachachan")
        result shouldBe (List("cha","cha","cha"), "n")
      }
      "dado un parser char('c') de kleene+ que recibe burt, retorna Failure" in {
        val Failure(exception) = charPlusParser.parse("burt")
        exception.getMessage shouldBe "El elemento parseado no satisface la condicion dada"
      }
    }
    "sepBy" - {
      val integer = number.+
      val numeroDeTelefono = integer.sepBy(char('-'))
      "dado un parser number de kleene+ separados por - que recibe 4356-1234, retorna Success con (List(List(4,3,5,6), List(1,2,3,4)), )" in {
        val Success(result) = numeroDeTelefono.parse("4356-1234")
        result shouldBe (List(List(4,3,5,6), List(1,2,3,4)), "")
      }
      "dado un parser number de kleene+ por - que recibe 4356 1234, retorna Failure" in {
        val Failure(exception) = numeroDeTelefono.parse("4356 1234")
        exception.getMessage shouldBe "El elemento no satisface el parser concatenado"
      }
    }
    "const" - {
      val trueParser = string("true").const(true)
      "dado un parser string(true) con constantes el valor bool true que recibe true (str), retorna Success con (true (bool), )" in {
        val Success(result) = trueParser.parse("true")
        result shouldBe (true, "")
      }
      "dado un parser string(true) con constantes el valor bool true que recibe untrue (str), retorna Failure" in {
        val Failure(exception) = trueParser.parse("untrue")
        exception.getMessage shouldBe "El elemento no empieza con el string a buscar"
      }
    }
    "map" - {
      case class Persona(nombre: String, apellido: String)
      val personaParser = (alphaNum.* <> (char(' ') ~> alphaNum.*))
        .map { case (nombre, apellido) => Persona(nombre.mkString, apellido.mkString) }
      "dado un parser a Persona que recibe Nestor Ortigoza, retorna Success con Persona(Nestor, Ortigoza)" in {
        val Success((value, rest)) = personaParser.parse("Nestor Ortigoza")
        value.nombre shouldBe "Nestor"
        value.apellido shouldBe "Ortigoza"
        rest shouldBe ""
      }
    }
  }
}
