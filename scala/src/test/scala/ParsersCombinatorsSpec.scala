import org.scalatest.matchers.should.Matchers._
import org.scalatest.freespec.AnyFreeSpec
import parsersSimples._

class ParsersCombinatorsSpec extends AnyFreeSpec {

  "<|> toma el 1er parser que no falla" in {
    val aob = new charParser('a') <|> new charParser('b')
    aob.parsear("arbol").get shouldBe 'a'
  }

  "<|> toma el 2do parser que no falla" in {
    val aob = new charParser('a') <|> new charParser('b')
    aob.parsear("bort").get shouldBe 'b'
  }

  "<|> falla si ambos parsers fallan" in {
    val aob = new charParser('a') <|> new charParser('b')
    aob.parsear("casa").isFailure shouldBe true
  }

  "<|> falla si ambos parsers fallan con input vacio" in {
    val aob = new charParser('a') <|> new charParser('b')
    aob.parsear("").isFailure shouldBe true
  }
  //---------------------------------------------

}
