import org.scalatest.matchers.should.Matchers._
import org.scalatest.freespec.AnyFreeSpec
import parsersSimples._

class ParsersSimplesSpec extends AnyFreeSpec {

  "anyChar toma la 1ra letra" in {
    val parser = new anyChar()
    parser.parsear("hola").get shouldBe 'h'
  }

  "anyChar falla con input vacio" in {
    val parser = new anyChar()
    parser.parsear("").isFailure shouldBe true
  }
  //---------------------------------------------
  "charParser toma la letra correcta" in {
    val parser = new charParser('c')
    parser.parsear("chau").get shouldBe 'c'
  }

  "charParser falla con letra incorrecta" in {
    val parser = new charParser('c')
    parser.parsear("hola").isFailure shouldBe true
  }

  "charParser falla con input vacio" in {
    val parser = new charParser('c')
    parser.parsear("").isFailure shouldBe true
  }
  //---------------------------------------------
  "voidParser toma cualquier input no vacio y devuelve Unit" in {
    val parser = new voidParser()
    parser.parsear("hola").get shouldBe ()
  }

  "voidParser falla con input vacio" in {
    val parser = new voidParser()
    parser.parsear("").isFailure shouldBe true
  }
  //---------------------------------------------
  "letter toma la 1ra letra" in {
    val parser = new letter()
    parser.parsear("hola").get shouldBe 'h'
  }

  "letter falla con input que no empieza con letra" in {
    val parser = new letter()
    parser.parsear("1234").isFailure shouldBe true
  }

  "letter falla con input vacio" in {
    val parser = new letter()
    parser.parsear("").isFailure shouldBe true
  }
  //---------------------------------------------
  "digit toma el 1er digito" in {
    val parser = new digit()
    parser.parsear("1234").get shouldBe '1'
  }

  "digit falla con input que no empieza con digito" in {
    val parser = new digit()
    parser.parsear("hola").isFailure shouldBe true
  }

  "digit falla con input vacio" in {
    val parser = new digit()
    parser.parsear("").isFailure shouldBe true
  }
  //---------------------------------------------
  "alphaNum toma la 1ra letra" in {
    val parser = new alphaNum()
    parser.parsear("hola1234").get shouldBe 'h'
  }

  "alphaNum toma el 1er digito" in {
    val parser = new alphaNum()
    parser.parsear("1234hola").get shouldBe '1'
  }

  "alphaNum falla con input que no empieza con letra ni digito" in {
    val parser = new alphaNum()
    parser.parsear("+-*/").isFailure shouldBe true
  }

  "alphaNum falla con input vacio" in {
    val parser = new alphaNum()
    parser.parsear("").isFailure shouldBe true
  }
  //---------------------------------------------
  "string toma el string correcto" in {
    val parser = new string("hola")
    parser.parsear("holamundo").get shouldBe "hola"
  }

  "string falla con string incorrecto" in {
    val parser = new string("hola")
    parser.parsear("chaumundo").isFailure shouldBe true
  }

  "string falla con input vacio" in {
    val parser = new string("hola")
    parser.parsear("").isFailure shouldBe true
  }
}
