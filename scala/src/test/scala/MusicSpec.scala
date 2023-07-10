import BasicParsers.char
import MusicParser._
import Musica._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.util.Success

class MusicSpec extends AnyFreeSpec {

  "Silencio" in {
    val Success((silencioBlanca, _)) = silencio.parse("_")
    val Success((silencioNegra, _)) = silencio.parse("-")
    val Success((silencioCorchea, _)) = silencio.parse("~")

    silencioBlanca shouldBe Silencio(Blanca)
    silencioNegra shouldBe Silencio(Negra)
    silencioCorchea shouldBe Silencio(Corchea)
  }

  "Sonido" in {
    val Success((unSonido, _)) = sonido.parse("5C#1/16")
    unSonido shouldBe Sonido(Tono(5, Cs), SemiCorchea)
  }

  "figura" in {
    val Success((unSonido, _)) = (figura <~ char('+')).parse("1/16+")
    unSonido shouldBe SemiCorchea
  }

  "Acorde" - {
    "Acorde con calidad" in {
      val Success((unAcorde, _)) = acordeConCalidad.parse("5CM1/4")
      unAcorde shouldBe C.acordeMayor(5, Negra)
    }
    "Acorde con notas" in {
      val Success((unAcorde, _)) = acordeConNotas.parse("6A+6C+6G1/8")
      unAcorde shouldBe Acorde(List(Tono(6, A), Tono(6, C), Tono(6, G)), Corchea)
    }
  }

  "Melodia" - {

    val melodiaSimple = List(Sonido(Tono(4, F), Corchea), Sonido(Tono(4, A), Corchea))
    "si recibo melodiaSimple deberia obtener la melodia de ejemplo" - {
      val Success((unaMelodia, _)) = melodia.parse("4F1/8 4A1/8")
      unaMelodia shouldBe melodiaSimple
    }

    val melodiaDeEjemplo = List(
      Sonido(Tono(4, F), Corchea),
      Sonido(Tono(4, A), Corchea),
      Gs.acordeMayor(4, Negra),
      Sonido(Tono(4, F), Corchea),
      Silencio(Blanca),
      Sonido(Tono(4, B), Blanca),
      Sonido(Tono(4, F), Corchea),
      Sonido(Tono(4, A), Corchea),
      Sonido(Tono(4, B), Negra),
      Sonido(Tono(5, C.sostenido), SemiCorchea),
      Sonido(Tono(5, D.sostenido), Negra),
      Acorde(List(Tono(6, A), Tono(6, Cs), Tono(6, G)), Corchea),
    )
    val melodiaAParsear = "4F1/8 4A1/8 4AbM1/4 4F1/8 _ 4B1/2 4F1/8 4A1/8 4B1/4 5Cs1/16 5Ds1/4 6A+6C#+6G1/8"

    "si recibo 4F1/8 4A1/8 4AbM1/4 4F1/8 _ 4B1/2 4F1/8 4A1/8 4B1/4 5Cs1/16 5Ds1/4 5C1/2 deberia obtener la melodia de ejemplo" in {
      val Success((unaMelodia, _)) = melodia.parse(melodiaAParsear)
      unaMelodia shouldBe melodiaDeEjemplo
    }
  }
}
