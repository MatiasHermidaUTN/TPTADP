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

class Student
  has_one String, named: :full_name
  has_one Numeric, named: :grade

  def promoted
    self.grade > 8
  end

  def has_last_name(last_name)
    self.full_name.split(' ')[1] === last_name
  end
end

class StudentWithGrade
  has_one String, named: :full_name
  has_one Grade, named: :grade
end

class StudentWithManyGrades
  has_one String, named: :full_name
  has_many Grade, named: :grades
end
