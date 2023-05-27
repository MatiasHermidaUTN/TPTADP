require_relative './errors/AttrNotCorrectTypeError'

module ComplexityType
  def initialize(type, validations)
    @validations = validations
    @type = type
  end
end

class ComplexType
  include ComplexityType

  def validate!(value, attr_name)
    raise AttrNotCorrectTypeError.new(attr_name) unless value.is_a?(@type)
    unless @validations.nil?
      value.class.persistent_attributes.each do |key, db_type|
        if key != :id
          db_type.validate!(value.attributes_hash, key)
        end
      end
    end
  end

  def save!(hash_all_attr, attr_name)
    hash_all_attr[attr_name] = hash_all_attr[attr_name].save!
    hash_all_attr
  end

  def refresh!(saved_value, attr_name, instance)
    instance.send(attr_name.to_s + "=", @type.find_by_id(saved_value).first)
  end

  def all_instances(saved_value, instance)
    @type.find_by_id(saved_value).first
  end
end

class SimpleType
  include ComplexityType

  def validate!(value, attr_name)
    raise AttrNotCorrectTypeError.new(attr_name) unless value.is_a?(@type)
    unless @validations.nil?
      @validations.each do |validator|
        validator.validate!(value)
      end
    end
  end

  def save!(hash_all_attr, attr_name)
    hash_all_attr
  end

  def refresh!(saved_value, attr_name, instance)
    instance.send(attr_name.to_s + "=", saved_value)
  end

  def all_instances(saved_value, instance)
    saved_value
  end
end
