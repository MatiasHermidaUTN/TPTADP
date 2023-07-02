import Parser.{Parser, SuccessParse}

import scala.util.{Failure, Success, Try}

object ParsersHelper {

  def expandTry[T](result: Try[T], exception: Exception): T={
    result match {
      case Success(value) => value
      case Failure(_) => throw exception
    }
  }

  def orParsers[T](elementToParse: String, aParser: Parser[T], anotherParser: Parser[T], exception: Exception):SuccessParse[T] = {
    val result = aParser.parse(elementToParse) orElse anotherParser.parse(elementToParse)
    expandTry(result, exception)
  }

}
