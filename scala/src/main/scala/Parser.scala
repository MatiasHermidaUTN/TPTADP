import CombinableParsers.{concat, leftMost, or, rightMost}
import OperationsParsers.{constant, kleene, mapped, optional, plus, satisfy, separator}
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
    def <>[U](otherParser: Parser[U]): Parser[(T,U)] = concat(this, otherParser)
    def ~>[U](otherParser: Parser[U]): Parser[U] = rightMost(this, otherParser)
    def <~[U](otherParser: Parser[U]): Parser[T] = leftMost(this, otherParser)

    def satisfies(condition: T => Boolean): Parser[T] = satisfy(this, condition)
    def opt(): Parser[Option[T]] = optional(this)
    def *(): Parser[List[T]] = kleene(this)
    def +(): Parser[List[T]] = plus(this)

    def sepBy[U](sepParser: Parser[U]): Parser[List[T]] = separator(this, sepParser)
    def const[U](value: U): Parser[U] = constant(this, value)
    def map[U](transform: T => U): Parser[U] = mapped(this, transform)

  }
}
