import parsersSimples._

case object Prueba extends App{

  private val parser = new anyChar()
  println(parser.parsear("hola").isSuccess)
  println(parser.parsear("hola").get)

  private val parser2 = new anyChar()
  println(parser2.parsear("").isFailure)
  println(parser2.parsear("").failed.get.getMessage)

  println("----------------------------------")

  private val parser3 = new charParser('c')
  println(parser3.parsear("chau").isSuccess)
  println(parser3.parsear("chau").get)

  private val parser4 = new charParser('c')
  println(parser4.parsear("hola").isFailure)
  println(parser4.parsear("hola").failed.get.getMessage)

  private val parser5 = new charParser('c')
  println(parser5.parsear("").isFailure)
  println(parser5.parsear("").failed.get.getMessage)

  println("----------------------------------")

  private val parser6 = new voidParser()
  println(parser6.parsear("hola").isSuccess)
  println(parser6.parsear("hola").get)

  private val parser7 = new voidParser()
  println(parser7.parsear("").isFailure)
  println(parser7.parsear("").failed.get.getMessage)

}

