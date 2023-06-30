import scala.util.{Try}

case class successParse[T](parsedValue: T, rest: String)

abstract class Parser[T] {
  def opt() = optionalParser(this)
  def parse(text: String): Try[successParse[T]] = {
    Try {
      if ("".equals(text)) {
        throw new RuntimeException("Fallo")
      }
      return parseo(text)
    }
  }
  def parseo(text: String): Try[successParse[T]]
  def <|>[U](otherParser: Parser[U]) = or(this, otherParser)
  def <>[U](otherParser: Parser[U]) = concat(this, otherParser)
  def ~>[U](otherParser: Parser[U]) = rightMost(this, otherParser)
  def <~[U](otherParser: Parser[U]) = leftMost(this, otherParser)
  def satisfies(condition: String => Boolean): satisfiesParser[T] = satisfiesParser(this, condition)
  def *(): Parser[List[T]] = kleeneParser(this)
  def +(): Parser[List[T]] = strictKleeneParser(this.*)
  def sepBy[U](separator: Parser[U]) = sepByParser(this, separator)
  def const[U](constant: U) = constParser(this, constant)
  def map[U](mapper: T => U) = mapParser(this, mapper)
}