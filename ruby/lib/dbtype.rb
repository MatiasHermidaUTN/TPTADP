require_relative './validations'

module DbType
    attr_accessor :name, :type, :validations, :default
    def initialize(type, description)
        @name = description[:named]
        @type = type
        @default = description[:default]
        @validations = description.reduce([]) { |result, (validation, value)|
            if (validation == :no_blank)
                result << NoBlankValidation.new
            end
            if (validation == :from)
                result << FromValidation.new(value)
            end
            if (validation == :to)
                result << ToValidation.new(value)
            end
            if (validation == :validate)
                result << ValidateValidation.new(value)
            end
            result
        }
    end
end
class OneComplexDbType
    include DbType
    def validate!(hash_all_attr, attr_name)
        value = hash_all_attr[attr_name]
        if value.nil? && !@default.nil? then value = hash_all_attr[attr_name] = @default end
        raise AttrNotCorrectTypeError.new(attr_name) unless value.is_a?(@type)
        if (!@validations.nil?)
            value.class.persistent_attributes.each do |key, db_type|
                if (key != :id)
                    db_type.validate!(value.attributes_hash, key)
                end
            end
        end
        hash_all_attr
    end
end

class ManyComplexDbType
    include DbType
    def validate!(hash_all_attr, attr_name)
        array = hash_all_attr[attr_name]
        if array.nil? && !@default.nil? then array = hash_all_attr[attr_name] = @default end
        array.each do |value|
            raise AttrNotCorrectTypeError.new(attr_name) unless value.is_a?(@type)
            if (!@validations.nil?)
                value.class.persistent_attributes.each do |key, db_type|
                    if (key != :id)
                        db_type.validate!(value.attributes_hash, key)
                    end
                end
            end
        end
        hash_all_attr
    end
end

class OneSimpleDbType
    include DbType
    def validate!(hash_all_attr, attr_name)
        value = hash_all_attr[attr_name]
        if value.nil? && !@default.nil? then value = hash_all_attr[attr_name] = @default end
        raise AttrNotCorrectTypeError.new(attr_name) unless value.is_a?(@type)
        if (!@validations.nil?)
            @validations.each do |validator|
                validator.validate!(value)
            end
        end
        hash_all_attr
    end
end

class ManySimpleDbType
    include DbType
    def validate!(hash_all_attr, attr_name)
        array = hash_all_attr[attr_name]
        if array.nil? && !@default.nil? then array = hash_all_attr[attr_name] = @default end
        array.each do |value|
            raise AttrNotCorrectTypeError.new(attr_name) unless value.is_a?(@type)
            if (!@validations.nil?)
                @validations.each do |validator|
                    validator.validate!(value)
                end
            end
        end
        hash_all_attr
    end
end