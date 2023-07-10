import scala.util.{Failure, Success, Try}

object ParsersHelper {

  def expandTry[T](result: Try[T], exception: Exception): T={
    result match {
      case Success(value) => value
      case Failure(_) => throw exception
    }
  }

}
