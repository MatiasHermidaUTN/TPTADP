import CombinableParsers.{concat, leftMost, or, rightMost}
import OperationsParsers.{satisfy}
import ExceptionsHelper.throwExceptionIfCondition

import scala.util.Try

object Parser {
  type SuccessParse[T] = (T, String)

  type ParseFunction[T] = String => SuccessParse[T]

  abstract class Parser[T]{
    def parse(elementToParse: String): Try[SuccessParse[T]] = {
      Try {
        throwExceptionIfCondition(
          elementToParse.isEmpty,
          new RuntimeException("No se encontro ningun caracter")
        )
        parseFunction(elementToParse)
      }
    }

    def parseFunction(elementToParse: String): SuccessParse[T]

    def <|>(otherParser: Parser[T]): Parser[T] = or(this, otherParser)
    def <>(otherParser: Parser[T]): Parser[(T,T)] = concat(this, otherParser)
    def ~>(otherParser: Parser[T]): Parser[T] = rightMost(this, otherParser)
    def <~(otherParser: Parser[T]): Parser[T] = leftMost(this, otherParser)

    def satisfies(condition: T => Boolean): Parser[T] = satisfy(this, condition)

  }
}
