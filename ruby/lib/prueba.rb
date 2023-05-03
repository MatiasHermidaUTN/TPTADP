require 'tadb'

class Module
  def has_one(type, description)
    name = description[:named]
    attr_accessor name
    persistent_attrs_array = singleton_methods(false).include?(:persistent_attrs) ? singleton_method(:persistent_attrs).call.push(name) : [name]
    define_singleton_method(:persistent_attrs) do
      persistent_attrs_array
    end
  end

  def all_instances
    table = TADB::DB.table(self.to_s)
    table.entries.collect do |attributes|
      obj = self.new
      attributes.each do |attr, value|
        if attr != :id
          obj.send(attr.to_s+"=", value)
        end
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
        obj.send(message) === value
      end
    else
      super
    end
  end

  def respond_to_missing?(name, include_private = false)
    responds_to_find_by(name) || super
  end

end
class Object
  def save!
    table = TADB::DB.table(self.class.to_s)
    entry = {}
    self.class.singleton_method(:persistent_attrs).call.each { |name| entry[name] = send(name)  }
    id = table.insert(entry)
    if singleton_methods(false).include?(:id)
      table.delete(self.id)
    end
    define_singleton_method(:id) do
      id
    end
  end

  def refresh!
    table = TADB::DB.table(self.class.to_s)
    entry = table.entries.find { |entry| entry[:id] === id }
    entry.each do |attr, value|
      if attr != :id
        send(attr.to_s+"=", value)
      end
    end
  end

  def forget!
    table = TADB::DB.table(self.class.to_s)
    table.delete(id)
    define_singleton_method(:id) do
      nil
    end
  end
end

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