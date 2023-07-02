import Musica.Melodia
import Parser.{Parser, SuccessParse}

object MusicParser {
  case class melodia() extends Parser[Melodia] {
    override def parseFunction(elementToParse: String): SuccessParse[Melodia] = {
      ???
    }
  }
}
