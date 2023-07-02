import BasicParsers.{alphaNum, anyChar, char, digit, letter, string, void}
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
      val char = new char('c')
      "recibe el string chau, retorna Success con char c" in {
        val Success(result) = char.parse("chau")
        result shouldBe ('c', "hau")
      }
      "recibe el string hola, retorna Failure" in {
        val Failure(exception) = char.parse("hola")
        exception.getMessage shouldBe "El elemento no empieza con el caracter a buscar"
      }
    }
    "void Parser" - {
      val void = new void()
      "recibe el string hola, retorna Success con Unit" in {
        val Success(result) = void.parse("hola")
        result shouldBe ((), "ola")
      }
    }
    "letter Parser" - {
      val letter = new letter()
      "recibe el string hola, retorna Success con char h" in {
        val Success(result) = letter.parse("hola")
        result shouldBe ('h', "ola")
      }
      "recibe el string 1234, retorna Failure" in {
        val Failure(exception) = letter.parse("1234")
        exception.getMessage shouldBe "El elemento no empieza con una letra"
      }
    }
    "digit Parser" - {
      val digit = new digit()
      "recibe el string 1234, retorna Success con char 1" in {
        val Success(result) = digit.parse("1234")
        result shouldBe ('1', "234")
      }
      "recibe el string hola, retorna Failure" in {
        val Failure(exception) = digit.parse("hola")
        exception.getMessage shouldBe "El elemento no empieza con un digito"
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
      val plusParser = char('c').+
      "dado un parser char('c') de kleene que recibe cccasa, retorna Success con (List('c','c','c'), asa)" in {
        val Success(result) =plusParser.parse("cccasa")
        result shouldBe (List('c','c','c'), "asa")
      }
      "dado un parser char('c') de kleene que recibe burt, retorna Failure" in {
        val Failure(exception) = plusParser.parse("burt")
        exception.getMessage shouldBe "El elemento no empieza con el caracter a buscar"
      }
    }
    "sepBy" - {
      val integer = digit().+

    }
  }
}
