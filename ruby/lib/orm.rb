require 'tadb'
require_relative './boolean'

module ORM

  def has_one(type, description)
    attribute_name = description[:named]
    attr_accessor attribute_name
    add_persistent_attribute!(attribute_name => type)
  end

  def persistent_attributes
    @persistent_attributes || {}
  end

  def add_persistent_attribute!(persistent_attribute)
    @persistent_attributes = persistent_attributes.merge(persistent_attribute)
  end

end

Class.include(ORM)
