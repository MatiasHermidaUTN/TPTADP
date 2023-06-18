package parsersSimples

import scala.util.Try

class string(texto: String) extends Parser(){
  override def parsear(input: String): Try[Any] = {
    Try(input).map {
      case palabra: String if palabra.substring(0, texto.length).equals(texto)
              => palabra.substring(0, texto.length)
      case _ => throw new Exception("No se encontro el texto")
    }
  }
}
