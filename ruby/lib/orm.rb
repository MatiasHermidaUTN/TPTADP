require 'tadb'
require_relative './boolean'

module ORM

  def has_one(type, description)
    attribute_name = description[:named]
    attr_accessor attribute_name
    add_persistent_attribute!(attribute_name => type)
  end

  def persistent_attributes
    @persistent_attributes || { id: String }
  end

  def add_persistent_attribute!(persistent_attribute)
    @persistent_attributes = persistent_attributes.merge(persistent_attribute)
  end

  def all_instances
    table = TADB::DB.table(self.name)
    all_instances = []
    table.entries.collect do |hash|
      instance = self.new
      hash.each do |key, value|
        instance.send("#{key}=", value)
      end
      all_instances.push(instance)
    end
    return all_instances
  end

  def responds_to_find_by(name)
    name.to_s.start_with?("find_by_")
  end

  def method_missing(name, *args)
    if responds_to_find_by(name)
      value = args[0]
      message = name.to_s.delete_prefix("find_by_")
      self.all_instances.select do |obj|
        if (obj.method(message).arity != 0)
          super
        end
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

Class.include(ORM)
