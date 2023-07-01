object ExceptionsHelper {
  def throwExceptionIfCondition(condition: Boolean, exception: Exception): Unit = {
    if(condition) throw exception
  }
}
