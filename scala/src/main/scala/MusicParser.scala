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

    private def matchTono(tono: ((Int, Nota), Option[Char])): Tono = {
      val ((octava, nota), alteration) = tono
      alteration match {
        case None => Tono(octava, nota)
        case Some('#') => Tono(octava, nota.sostenido)
        case Some('s') => Tono(octava, nota.sostenido)
        case Some('b') => Tono(octava, nota.bemol)
        case _ => throw new RuntimeException("La alteracion no existe")
      }
    }

    override def parseFunction(elementToParse: String): SuccessParse[Tono] = {
      val tonoParser = number() <> nota() <> alteracion.opt
      val (value, rest) = tonoParser.parseFunction(elementToParse)
      (matchTono(value), rest)
    }
  }

  case class nota() extends Parser[Nota] {
    private def matchNoteChar(note: Char): Nota = {
      note match {
        case 'C' => C
        case 'D' => D
        case 'E' => E
        case 'F' => F
        case 'G' => G
        case 'A' => A
        case 'B' => B
        case _ => throw new RuntimeException("La nota no existe")
      }
    }
    override def parseFunction(elementToParse: String): (Nota, String) = {
      val (value, rest) = letter().parseFunction(elementToParse)
      (matchNoteChar(value), rest)
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
