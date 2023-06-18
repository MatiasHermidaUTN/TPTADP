package parsersSimples

import scala.util.Try

class OneChar(letra: Char, input: String) extends Parser(input){
  override def parsear(): Try[Any] = {
    //val anyChar = new AnyChar(input)  //se puede usar esto?

    if (!input.isEmpty && input.head == letra)
      Try(input.head)
    else
      Try(throw new Exception("No se encontro la letra"))
  }
}
