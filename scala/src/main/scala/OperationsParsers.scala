import ExceptionsHelper.throwExceptionIfCondition
import Parser.{Parser, SuccessParse}

object OperationsParsers {
  case class satisfy[T](parser: Parser[T], condition: T => Boolean) extends Parser[T] {
    override def parseFunction(elementToParse: String): SuccessParse[T] = {
      val result = parser.parseFunction(elementToParse)
      throwExceptionIfCondition(
        condition(result._1),
        new RuntimeException("El elemento parseado no satisface la condicion dada")
      )
      result
    }
  }
}
