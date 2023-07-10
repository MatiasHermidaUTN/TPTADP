import BasicParsers.{char, number, string}
import Musica._

object MusicParser {

  val silencio = char('_').const(Silencio(Blanca)) <|> char('-').const(Silencio(Negra)) <|> char('~').const(Silencio(Corchea))

  val alteracion =
      char('#').const {  (unaNota: Nota) => unaNota.sostenido } <|>
      char('s').const {  (unaNota: Nota) => unaNota.sostenido } <|>
      char('b').const {  (unaNota: Nota) => unaNota.bemol }

  val nota = ((
      char('C').const(C) <|>
      char('D').const(D) <|>
      char('E').const(E) <|>
      char('F').const(F) <|>
      char('G').const(G) <|>
      char('A').const(A) <|>
      char('B').const(B)
    ) <> alteracion.opt).map {
    case (note, None) => note
    case (note, Some(alteracionFn)) => alteracionFn(note)
  }

  val tono = (number <> nota).map{ case (octava, nota) => Tono(octava, nota) }

  val figura =
    string("1/16").const(SemiCorchea) <|>
    string("1/1").const(Redonda) <|>
    string("1/2").const(Blanca) <|>
    string("1/4").const(Negra) <|>
    string("1/8").const(Corchea)

  val sonido = (tono <> figura).map { case (unTono, unaFigura) => Sonido(unTono, unaFigura) }

  val calidad =
    char('m').const { (unaNota : Nota, octava: Int, unaFigura: Figura) => unaNota.acordeMenor(octava, unaFigura) } <|>
    char('M').const { (unaNota : Nota, octava: Int, unaFigura: Figura) => unaNota.acordeMayor(octava, unaFigura) }

  val acordeConCalidad = (number <> nota <> calidad <> figura).map {
    case (((octava ,unaNota), acordeFn), unaFigura) => acordeFn(unaNota, octava, unaFigura)
  }

  val acordeConNotas = ((tono sepBy char('+')) <> figura).map {
    case ( tonos, unaFigura ) => Acorde(tonos, unaFigura)
  }

  val acorde = acordeConCalidad <|> acordeConNotas
  val tocable = silencio <|> sonido <|> acorde

  val melodia = tocable sepBy char(' ')

}
