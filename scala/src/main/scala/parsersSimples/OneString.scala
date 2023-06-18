package parsersSimples

import scala.util.Try

class OneString(texto: String, input: String) extends Parser(input){
  override def parsear(): Try[Any] = {
    if (!input.isEmpty && input.substring(0, texto.length).equals(texto))
      Try(input.substring(0, texto.length))
    else
      Try(throw new Exception("No se encontro el texto"))
  }
}
