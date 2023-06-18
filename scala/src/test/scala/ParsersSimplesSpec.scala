import org.scalatest.matchers.should.Matchers._
import org.scalatest.freespec.AnyFreeSpec
import parsersSimples._

class ParsersSimplesSpec extends AnyFreeSpec {

  "AnyChar toma la 1ra letra" in {
    val parser = new AnyChar("hola")
    parser.parsear().get shouldBe 'h'
  }

  "AnyChar falla con input vacio" in {
    val parser = new AnyChar("")
    parser.parsear().isFailure shouldBe true
  }
  //---------------------------------------------
  "OneChar toma la letra correcta" in {
    val parser = new OneChar('c', "chau")
    parser.parsear().get shouldBe 'c'
  }

  "OneChar falla con letra incorrecta" in {
    val parser = new OneChar('c', "hola")
    parser.parsear().isFailure shouldBe true
  }

  "OneChar falla con input vacio" in {
    val parser = new OneChar('c', "")
    parser.parsear().isFailure shouldBe true
  }
  //---------------------------------------------
  "Void toma cualquier input no vacio y devuelve Unit" in {
    val parser = new Void("hola")
    parser.parsear().get shouldBe ()
  }

  "Void falla con input vacio" in {
    val parser = new Void("")
    parser.parsear().isFailure shouldBe true
  }
  //---------------------------------------------
  "Letter toma la 1ra letra" in {
    val parser = new Letter("hola")
    parser.parsear().get shouldBe 'h'
  }

  "Letter falla con input que no empieza con letra" in {
    val parser = new Letter("123")
    parser.parsear().isFailure shouldBe true
  }

  "Letter falla con input vacio" in {
    val parser = new Letter("")
    parser.parsear().isFailure shouldBe true
  }
  //---------------------------------------------
  "Digit toma el 1er digito" in {
    val parser = new Digit("123")
    parser.parsear().get shouldBe '1'
  }

  "Digit falla con input que no empieza con digito" in {
    val parser = new Digit("hola")
    parser.parsear().isFailure shouldBe true
  }

  "Digit falla con input vacio" in {
    val parser = new Digit("")
    parser.parsear().isFailure shouldBe true
  }
  //---------------------------------------------
  "AlphaNum toma la 1ra letra" in {
    val parser = new AlphaNum("hola")
    parser.parsear().get shouldBe 'h'
  }

  "AlphaNum toma el 1er digito" in {
    val parser = new AlphaNum("123")
    parser.parsear().get shouldBe '1'
  }

  "AlphaNum falla con input que no empieza con letra ni digito" in {
    val parser = new AlphaNum("+-*/")
    parser.parsear().isFailure shouldBe true
  }

  "AlphaNum falla con input vacio" in {
    val parser = new AlphaNum("")
    parser.parsear().isFailure shouldBe true
  }
  //---------------------------------------------
  "OneString toma el string correcto" in {
    val parser = new OneString("hola", "holamundo")
    parser.parsear().get shouldBe "hola"
  }

  "OneString falla con string incorrecto" in {
    val parser = new OneString("hola", "chau mundo")
    parser.parsear().isFailure shouldBe true
  }

  "OneString falla con input vacio" in {
    val parser = new OneString("hola", "")
    parser.parsear().isFailure shouldBe true
  }
  //---------------------------------------------

}
