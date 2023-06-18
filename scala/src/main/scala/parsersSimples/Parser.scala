package parsersSimples

import scala.util.Try

abstract class Parser {
  def parsear(input: String): Try[Any]

  def <|> (p2: Parser): Parser = {
    val p1 = this
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p1.parsear(input) orElse p2.parsear(input)
      }
    }
  }

  private def recortarInput(input: String, result: Any): String = {
    result match {
      case result: String => input.substring(result.length)
      case result: Char => input.substring(1)
      case result: Unit => input.substring(1)
      case result: (_, _) =>
        val inputAux = recortarInput(input, result._1)
        recortarInput(inputAux, result._2)
    }
  }

  def <> (p2: Parser): Parser = {
    val p1 = this
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p1.parsear(input).flatMap { result1 =>
          p2.parsear(recortarInput(input, result1)).map { result2 =>
            (result1, result2)
          }
        }
      }
    }
  }

  def ~> (p2: Parser): Parser = {
    val p1 = this
    val parser = p1 <> p2
    new Parser {
      override def parsear(input: String): Try[Any] = {
        ///*
        parser.parsear(input).map{
          case (v1, v2) =>
            println(v1)
            println(v2)
            v2
        }
        //*/
//        p1.parsear(input).flatMap { result1 =>
//          p2.parsear(recortarInput(input, result1))
//        }
      }
    }
  }

  def <~ (p2: Parser): Parser = {
    val p1 = this
    new Parser {
      override def parsear(input: String): Try[Any] = {
        p1.parsear(input).flatMap { result1 =>
          p2.parsear(recortarInput(input, result1)).map { _ =>
            result1
          }
        }
      }
    }
  }
}
