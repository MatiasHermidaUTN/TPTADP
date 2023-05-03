require 'tadb'

module Boolean

end

class TrueClass
  include Boolean
end

class FalseClass
  include Boolean
end

module Persistent
  @@simple_types = [Numeric, String, Boolean]
  def save!

    table = TADB::DB.table(self.class.to_s)
    persistent_attrs_hash = self.class.singleton_method(:persistent_attrs).call

    entry = {}
    persistent_attrs_hash.each do |name, options|
      type = options[:type]
      entry[name] = @@simple_types.include?(type) ? send(name) : send(name).save!
    end

    if singleton_methods(false).include?(:id)
      entry = entry.merge({:id=>self.id})
      table.delete(self.id)
      table.insert(entry)
      self.id
    else
      id = table.insert(entry)
      define_singleton_method(:id) do
        id
      end
      define_singleton_method(:id=) do |id|
        id
      end
      id
    end

  end

  def refresh!
    table = TADB::DB.table(self.class.to_s)
    entry = table.entries.find { |entry| entry[:id] == id }
    entry.each do |attr, value|
      if attr == :id
        next
      end
      type = self.class.singleton_method(:persistent_attrs).call[attr][:type]
      value = @@simple_types.include?(type) ? value : type.find_by_id(value)[0]
      send(attr.to_s+"=", value)
    end
    self
  end

  def forget!
    table = TADB::DB.table(self.class.to_s)
    table.delete(id)
    define_singleton_method(:id) do
      nil
    end
    self.class.singleton_method(:persistent_attrs).call.each do |name,options|
      if @@simple_types.include?(options[:type])
        next
      end
      send(name).forget!
    end
  end
end

class Module

  def has_one(type, description)
    #TODO: Agregar validaciones si es de tipo persistible
    name = description[:named]
    attr_accessor name
    attr_options_hash = {name => { :type => type }}
    # Si ya existe el metodo persistent_attrs (es decir, ya se agrego con has_one otro atributo) se mergea al estado actual del hash. Si es falso, se inicializa con la variable attr_options_hash
    persistent_attrs_hash = singleton_methods(false).include?(:persistent_attrs) ? singleton_method(:persistent_attrs).call.merge(attr_options_hash) : attr_options_hash
    define_singleton_method(:persistent_attrs) do
      persistent_attrs_hash
    end
    self.send(:include, Persistent)
  end

  def all_instances
    table = TADB::DB.table(self.to_s)
    obj = self.new
    obj.define_singleton_method(:id=) do |id|
      id
    end
    table.entries.collect do |attributes|
      attributes.each do |attr, value|
        if attr == :id
          obj.define_singleton_method(:id) do
            value
          end
        end
        obj.send(attr.to_s+"=", value)
      end
      obj
    end
  end

  private def responds_to_find_by(name)
    name.to_s.start_with?("find_by_")
  end

  def method_missing(name, *args)
    if responds_to_find_by(name)
      value = args[0]
      message = name.to_s.delete_prefix("find_by_")
      self.all_instances.select do |obj|
        obj.send(message) == value
      end
    else
      super
    end
  end

  def respond_to_missing?(name, include_private = false)
    responds_to_find_by(name) || super
  end

end

class Grade
  has_one Numeric, named: :value
end

class Student
  has_one String, named: :full_name
  has_one Grade, named: :grade
end

s = Student.new
s.full_name = "leo sbaraglia"
s.grade = Grade.new
s.grade.value = 8
s.save! # Salva al estudiante Y su nota
g = s.grade # Retorna Grade(8)
g.value
g.value = 5
g.save!
s.refresh!.grade.value # Retorna Grade(5)
s.forget!

=begin
class Person
  has_one String, named: :first_name
  has_one String, named: :last_name
  has_one String, named: :address
  has_one Numeric, named: :age
end

class Point
  has_one Numeric, named: :x
end

p1 = Person.new
p1.first_name = "pepe"
p1.last_name = "papo"
p1.address = "calle false 123"
p1.age = 56
p1.save!
puts "P1 id! 1er " + p1.id
p1.save!
puts "P1 id! 2do " + p1.id

p1.first_name = "rico"
puts "P1 Antes de refresh! " + p1.first_name

p1.refresh!
puts "P1 Despues de refresh! " + p1.first_name

p2 = Person.new
p2.first_name = "carlos"
p2.last_name = "santos"
p2.address = "calle false 456"
p2.age = 25
p2.save!

puts "all_instances antes de forget! #{Person.all_instances}"
p2.forget!
puts "all_instances despues de forget! #{Person.all_instances}"

puts "find_by_first_name #{Person.find_by_first_name("pepe")}"
=end