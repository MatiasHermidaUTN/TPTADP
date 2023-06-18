import parsersSimples._

case object Prueba extends App{

  private val parser = new AnyChar("hola como estas")
  println(parser.parsear().isSuccess)
  println(parser.parsear().get)

  private val parser2 = new AnyChar("")
  println(parser2.parsear().isFailure)
  println(parser2.parsear().failed.get.getMessage)

  println("----------------------------------")

  private val parser3 = new OneChar('c', "chau")
  println(parser3.parsear().isSuccess)
  println(parser3.parsear().get)

  private val parser4 = new OneChar('c', "hola")
  println(parser4.parsear().isFailure)
  println(parser4.parsear().failed.get.getMessage)

  private val parser5 = new OneChar('c', "")
  println(parser5.parsear().isFailure)
  println(parser5.parsear().failed.get.getMessage)

  println("----------------------------------")

  private val parser6 = new AnyChar("hola")
  println(parser6.parsear().isSuccess)
  println(parser6.parsear().get)

  private val parser7 = new AnyChar("")
  println(parser7.parsear().isFailure)
  println(parser7.parsear().failed.get.getMessage)

}

