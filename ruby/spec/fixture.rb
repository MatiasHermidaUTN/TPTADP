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
  has_one Point, named: :ubicacion
  has_many Grade, named: :grades
end

#
module PersonMixin
  has_one String, named: :full_name
end

class StudentWithPersonMixin
  include PersonMixin
  has_one Grade, named: :grade
end

class AssistantProfessor < StudentWithPersonMixin
  has_one String, named: :type
end

#
class StudentWithValidations
    has_one String, named: :full_name, no_blank: true
    has_one Numeric, named: :age, from: 18, to: 100
    has_many Grade, named: :grades, validate: proc{ value > 2 }
end

#
class StudentWithDefault
  has_one String, named: :full_name, default: "natalia natalia"
  has_one Grade, named: :grade, default: Grade.new, no_blank: true
end

class Box
  has_many Numeric, named: :points
end

module Algo
  has_one String, named: :full_name
end

class Hola
  include Algo
end