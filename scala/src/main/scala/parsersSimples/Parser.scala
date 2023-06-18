package parsersSimples

import scala.util.Try

abstract class Parser(var input: String){
  def parsear(): Try[Any]
}
