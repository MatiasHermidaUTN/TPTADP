package parsersSimples

import scala.util.Try

class Combinators {
  def orElse2(p1: Parser, p2: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p1.parsear(input) orElse p2.parsear(input)
      }
    }
  }

  def andThen(p1: Parser, p2: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p1.parsear(input).flatMap( _ => p2.parsear(input) )
      }
    }
  }

  def many(p: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => many(p).parsear(input) )
      }
    }
  }

  def many1(p: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => many(p).parsear(input) )
      }
    }
  }

  def manyTill(p: Parser, end: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        end.parsear(input).flatMap( _ => Try(()) ) orElse
        p.parsear(input).flatMap( _ => manyTill(p, end).parsear(input) )
      }
    }
  }

  def sepBy(p: Parser, sep: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => sepBy1(p, sep).parsear(input) ) orElse
        Try( () )
      }
    }
  }

  def sepBy1(p: Parser, sep: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => sepBy1(p, sep).parsear(input) ) orElse
        p.parsear(input).flatMap( _ => Try( () ) )
      }
    }
  }

  def chainl(p: Parser, op: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => chainl1(p, op).parsear(input) )
      }
    }
  }

  def chainl1(p: Parser, op: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => chainl1(p, op).parsear(input) ) orElse
        p.parsear(input).flatMap( _ => Try( () ) )
      }
    }
  }

  def chainr(p: Parser, op: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => chainr1(p, op).parsear(input) )
      }
    }
  }

  def chainr1(p: Parser, op: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => chainr1(p, op).parsear(input) ) orElse
        p.parsear(input).flatMap( _ => Try( () ) )
      }
    }
  }

  def between(p: Parser, start: Parser, end: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        start.parsear(input).flatMap( _ => p.parsear(input) ).flatMap( _ => end.parsear(input) )
      }
    }
  }

  def manyTill1(p: Parser, end: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        end.parsear(input).flatMap( _ => Try( () ) ) orElse
        p.parsear(input).flatMap( _ => manyTill1(p, end).parsear(input) )
      }
    }
  }

  def skipMany(p: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => skipMany(p).parsear(input) ) orElse
        Try( () )
      }
    }
  }

  def skipMany1(p: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => skipMany(p).parsear(input) ) orElse
        p.parsear(input).flatMap( _ => Try( () ) )
      }
    }
  }

  def endBy(p: Parser, sep: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => sep.parsear(input) ) orElse
        Try( () )
      }
    }
  }

  def endBy1(p: Parser, sep: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => sep.parsear(input) ) orElse
        p.parsear(input).flatMap( _ => Try( () ) )
      }
    }
  }

  def sepEndBy(p: Parser, sep: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => sepEndBy1(p, sep).parsear(input) ) orElse
        Try( () )
      }
    }
  }

  def sepEndBy1(p: Parser, sep: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input).flatMap( _ => sepEndBy1(p, sep).parsear(input) ) orElse
        p.parsear(input).flatMap( _ => sep.parsear(input) )
      }
    }
  }

  def option(p: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input) orElse
        Try( () )
      }
    }
  }

  def optional(p: Parser): Parser = {
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p.parsear(input) orElse
        Try( () )
      }
    }
  }

}
