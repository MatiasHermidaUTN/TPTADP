class NotPersistedError < Exception
  attr_accessor :obj
  def initialize(obj)
    self.obj = obj
  end
end
