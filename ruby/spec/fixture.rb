class Person
  has_one String, named: :first_name
  has_one String, named: :last_name
  has_one Numeric, named: :age
  has_one Boolean, named: :admin

  attr_accessor :some_other_non_persistible_attribute
end

class Grade
  has_one String, named: :value
  has_one Numeric, named: :value
end


class Point
  has_one Numeric, named: :x
  has_one Numeric, named: :y
  def add(other)
    self.x = self.x + other.x
    self.y = self.y + other.y
  end
end