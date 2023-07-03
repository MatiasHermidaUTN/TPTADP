import BasicParsers.{anyChar, char, digit, letter, number}
import Musica._
import Parser.{Parser, SuccessParse}

object MusicParser {

  case class silencio() extends Parser[Tocable] {
    override def parseFunction(elementToParse: String): SuccessParse[Tocable] = {
      val silenceParser = anyChar()
      val (value, rest) = silenceParser.parseFunction(elementToParse)
      value match {
        case '_' => (Silencio(Blanca), rest)
        case '-' => (Silencio(Negra), rest)
        case '~' => (Silencio(Corchea), rest)
        case _ => throw new RuntimeException("El silencio no existe")
      }
    }
  }

  val alteracion = char('#') <|> char('s') <|> char('b')

  case class tono() extends Parser[Tono] {

    override def parseFunction(elementToParse: String): SuccessParse[Tono] = {
      val tonoParser = number() <> nota()
      val ((octava, note), rest) = tonoParser.parseFunction(elementToParse)
      (Tono(octava, note), rest)
    }
  }

  val notaChar = char('C') <|> char('D') <|> char('E') <|> char('F') <|> char('G') <|> char('A') <|> char('B')

  case class nota() extends Parser[Nota] {

    private def matchNoteChar(noteChar: Char, alteration: Option[Char]): Nota = {
      val note = noteChar match {
        case 'C' => C
        case 'D' => D
        case 'E' => E
        case 'F' => F
        case 'G' => G
        case 'A' => A
        case 'B' => B
        case _ => throw new RuntimeException("La nota no existe")
      }
      alteration match {
        case None => note
        case Some('#') => note.sostenido
        case Some('s') => note.sostenido
        case Some('b') => note.bemol
        case _ => throw new RuntimeException("La alteracion no existe")
      }
    }
    override def parseFunction(elementToParse: String): (Nota, String) = {
      val notaParser = notaChar <> alteracion.opt
      val ((note, alteration), rest) = notaParser.parseFunction(elementToParse)
      (matchNoteChar(note, alteration), rest)
    }
  }

  case class figura() extends Parser[Figura] {
    override def parseFunction(elementToParse: String): SuccessParse[Figura] = {
      val figuraParser = digit().+ <> (char('/') ~> digit().+)
      val ((num,den), rest) = figuraParser.parseFunction(elementToParse)
      s"${num.mkString}/${den.mkString}" match {
        case "1/1" => (Redonda, rest)
        case "1/2" => (Blanca, rest)
        case "1/4" => (Negra, rest)
        case "1/8" => (Corchea, rest)
        case "1/16" => (SemiCorchea, rest)
        case _ => throw new RuntimeException("La figura no existe")
      }
    }
  }

  case class sonido() extends Parser[Tocable] {
    override def parseFunction(elementToParse: String): SuccessParse[Tocable] = {
      val sonidoParser = tono() <> figura()
      val ((unTono, unaFigura), rest) = sonidoParser.parseFunction(elementToParse)
      (Sonido(unTono, unaFigura), rest)
    }
  }

  case class acordeConCalidad() extends Parser[Tocable] {
    override def parseFunction(elementToParse: String): SuccessParse[Tocable] = {
      val acordeParser = number() <> nota() <> letter() <> figura()
      val ((((octava ,unaNota), unaCalidad), unaFigura), rest) = acordeParser.parseFunction(elementToParse)
      unaCalidad match {
        case 'm' => (unaNota.acordeMenor(octava, unaFigura), rest)
        case 'M' => (unaNota.acordeMayor(octava, unaFigura), rest)
        case _ => throw new RuntimeException("La calidad no existe")
      }
    }
  }

  case class acordeConNotas() extends Parser[Tocable] {
    override def parseFunction(elementToParse: String): SuccessParse[Tocable] = {
      val acordeParser = (tono() <~ char('+')).+ <> tono() <> figura()
      val (((tonos, lastTono), unaFigura), rest) = acordeParser.parseFunction(elementToParse)
      (Acorde(tonos ::: List(lastTono), unaFigura), rest)
    }
  }

  val acorde = acordeConCalidad() <|> acordeConNotas()
  val tocable = silencio() <|> sonido() <|> acorde

  case class melodia() extends Parser[Melodia] {
    override def parseFunction(elementToParse: String): SuccessParse[Melodia] = {
      val melodiaParser = ( tocable <~ char(' ') ).* <> tocable
      val ((tocables, ultimoTocable), rest) = melodiaParser.parseFunction(elementToParse)
      (tocables ::: List(ultimoTocable), rest)
    }
  }

}
