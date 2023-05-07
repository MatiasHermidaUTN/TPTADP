require 'tadb'
require_relative './boolean'

module ORM

  def has_one(type, description)
    @all_instances = []
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
    @all_instances
  end
end

Class.include(ORM)
