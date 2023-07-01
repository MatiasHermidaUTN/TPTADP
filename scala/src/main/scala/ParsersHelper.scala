import Parser.{Parser, SuccessParse}

import scala.util.{Failure, Success, Try}

object ParsersHelper {

  private def expandTry[T](result: Try[T], exception: Exception): T={
    result match {
      case Success(value) => value
      case Failure(_) => throw exception
    }
  }

  def orParsers[T](elementToParse: String, aParser: Parser[T], anotherParser: Parser[T], exception: Exception):SuccessParse[T] = {
    val result = aParser.parse(elementToParse) orElse anotherParser.parse(elementToParse)
    expandTry(result, exception)
  }

  def concatParsers[T, U](elementToParse: String, aParser: Parser[T], anotherParser: Parser[T], yieldFn: (SuccessParse[T],SuccessParse[T]) => SuccessParse[U] ):SuccessParse[U] = {
    val result = for {
      result1 <- aParser.parse(elementToParse)
      result2 <- anotherParser.parse(result1._2)
    } yield yieldFn(result1, result2)
    expandTry(result, new RuntimeException("El elemento no satisface el parser concatenado"))
  }

}
