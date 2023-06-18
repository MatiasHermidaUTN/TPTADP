import org.scalatest.matchers.should.Matchers._
import org.scalatest.freespec.AnyFreeSpec
import parsersSimples._

class ParsersCombinatorsSpec extends AnyFreeSpec {

  "<|> toma el 1er parser que no falla" in {
    val aob = charParser('a') <|> charParser('b')
    aob.parsear("arbol").get shouldBe 'a'
  }

  "<|> toma el 2do parser que no falla" in {
    val aob = charParser('a') <|> charParser('b')
    aob.parsear("bort").get shouldBe 'b'
  }

  "<|> falla si ambos parsers fallan" in {
    val aob = charParser('a') <|> charParser('b')
    aob.parsear("casa").isFailure shouldBe true
  }

  "<|> falla si ambos parsers fallan con input vacio" in {
    val aob = charParser('a') <|> charParser('b')
    aob.parsear("").isFailure shouldBe true
  }

  "<|> de un string, un char y un string toma el 1ro que no falla" in {
    val aob = string("hola") <|> charParser('b') <|> string("barco")
    aob.parsear("barco").get shouldBe'b'
  }
  //---------------------------------------------
  "<> toma ambos parsers que no fallan" in {
    val holaMundo = string("hola") <> string("mundo")
    holaMundo.parsear("holamundo").get shouldBe ("hola", "mundo")
  }

  "<> falla si el 1er parser falla" in {
    val holaMundo = string("hola") <> string("mundo")
    holaMundo.parsear("chaumundo").isFailure shouldBe true
  }

  "<> falla si el 2do parser falla" in {
    val holaMundo = string("hola") <> string("mundo")
    holaMundo.parsear("holachau").isFailure shouldBe true
  }

  "<> falla si ambos parsers fallan" in {
    val holaMundo = string("hola") <> string("mundo")
    holaMundo.parsear("chau").isFailure shouldBe true
  }

  "<> falla si ambos parsers fallan con input vacio" in {
    val holaMundo = string("hola") <> string("mundo")
    holaMundo.parsear("").isFailure shouldBe true
  }

  "<> de un void y tres charParser devuelve tupla de tuplas" in {
    val ab = voidParser() <> charParser('b')
    val abc = ab <> charParser('c') <> charParser('d')
    abc.parsear("abcde").get shouldBe ((((), 'b'), 'c'), 'd')
  }
  //---------------------------------------------
  "~> toma ambos parsers que no fallan y devuelve el 2do" in {
    val holaMundo = string("hola") ~> string("mundo")
    holaMundo.parsear("holamundo").get shouldBe "mundo"
  }

  "~> falla si el 1er parser falla" in {
    val holaMundo = string("hola") ~> string("mundo")
    holaMundo.parsear("chaumundo").isFailure shouldBe true
  }

  "~> falla si el 2do parser falla" in {
    val holaMundo = string("hola") ~> string("mundo")
    holaMundo.parsear("holachau").isFailure shouldBe true
  }

  "~> falla si ambos parsers fallan con input vacio" in {
    val holaMundo = string("hola") ~> string("mundo")
    holaMundo.parsear("").isFailure shouldBe true
  }

  "~> de un void y un charParser devuelve el ultimo charParser" in {
    val ab = voidParser() ~> charParser('b')
    ab.parsear("abcde").get shouldBe 'b'
  }

  /*"~> de un void y dos charParser devuelve el ultimo charParser" in {
    val ab = voidParser() ~> charParser('b') ~> charParser('c')
    ab.parsear("abcde").get shouldBe 'c'
  }*/
  //---------------------------------------------
  "<~ toma ambos parsers que no fallan y devuelve el 1ro" in {
    val holaMundo = string("hola") <~ string("mundo")
    holaMundo.parsear("holamundo").get shouldBe "hola"
  }

  "<~ falla si el 1er parser falla" in {
    val holaMundo = string("hola") <~ string("mundo")
    holaMundo.parsear("chaumundo").isFailure shouldBe true
  }

  "<~ falla si el 2do parser falla" in {
    val holaMundo = string("hola") <~ string("mundo")
    holaMundo.parsear("holachau").isFailure shouldBe true
  }

  "<~ falla si ambos parsers fallan con input vacio" in {
    val holaMundo = string("hola") <~ string("mundo")
    holaMundo.parsear("").isFailure shouldBe true
  }

  "<~ de un void y un charParser devuelve el void" in {
    val ab = voidParser() <~ charParser('b')
    ab.parsear("abcde").get shouldBe ()
  }

  /*"<~ de un void y dos charParser devuelve el void" in {
    val ab = voidParser() <~ charParser('b') <~ charParser('c')
    ab.parsear("abcde").get shouldBe ()
  }*/
}
