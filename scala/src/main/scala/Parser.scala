import scala.util.{Try}

case class successParse(parsedValue: Any, rest: String)

abstract class Parser {
  def opt(): Parser = optionalParser(this)

  def parse(text: String): Try[successParse] = {
    Try {
      val trimmedText = text.trim
      if ("".equals(trimmedText)) {
        throw new RuntimeException("Fallo")
      }
      val parseResult = parseo(trimmedText)
      return returnParseo(parseResult, text)
    }
  }
  def returnParseo(result: Try[successParse], text: String): Try[successParse] = result
  def parseo(text: String): Try[successParse]
  def <|>(otherParser: Parser): Parser = or(this, otherParser)

  def <>(otherParser: Parser): Parser = concat(this, otherParser)

  def ~>(otherParser: Parser): Parser = rightMost(this, otherParser)

  def <~(otherParser: Parser): Parser = leftMost(this, otherParser)

  def satisfies(condition: String => Boolean) = satisfiesParser(this, condition)

  def *(): Parser = kleeneParser(this)

  def +(): Parser = strictKleeneParser(this.*)
}