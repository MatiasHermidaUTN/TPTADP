require_relative './validations'
require_relative './complexityType'

module DbType
    attr_accessor :name, :type, :validations, :default
    def initialize(type, description)
        @name = description[:named]
        @type = type
        validations = description.reduce([]) { |result, (validation, value)|
            if validation == :no_blank
                result.push(NoBlankValidation.new)
            end
            if validation == :from
                result.push(FromValidation.new(value))
            end
            if validation == :to
                result.push(ToValidation.new(value))
            end
            if validation == :validate
                result.push(ValidateValidation.new(value))
            end
            result
        }
        @complexity = [Numeric, String, Boolean].include?(type) ? SimpleType.new(type, validations) : ComplexType.new(type, validations)
        @default = description[:default]
    end

    def validate_default!(hash_all_attr, attr_name)
        if hash_all_attr[attr_name].nil? && !@default.nil? then hash_all_attr[attr_name] = @default end
    end
end

class OneDbType
    include DbType
    def validate!(hash_all_attr, attr_name)
        validate_default!(hash_all_attr, attr_name)
        @complexity.validate!(hash_all_attr[attr_name], attr_name)
        hash_all_attr
    end

    def save!(hash_all_attr, attr_name, instance)
        @complexity.save!(hash_all_attr, attr_name)
    end

    def refresh!(saved_value, attr_name, instance)
        @complexity.refresh!(saved_value, attr_name, instance)
    end

    def forget!(instance)
    end

    def all_instances(saved_value, instance)
        @complexity.all_instances(saved_value, instance)
    end
end

class ManyDbType
    include DbType
    def validate!(hash_all_attr, attr_name)
        validate_default!(hash_all_attr, attr_name)
        hash_all_attr[attr_name].each do |value|
            @complexity.validate!(value, attr_name)
        end
        hash_all_attr
    end

    def save!(hash_all_attr, attr_name, instance)
        hash_all_attr[:id] ||= SecureRandom.uuid
        hash_all_attr[attr_name].each do |value|
            id_complex = value.save!
            intermediate_table(instance).insert({ instance.class.name.to_sym => hash_all_attr[:id], @type.name.to_sym  => id_complex })
        end
        hash_all_attr.except(attr_name)
    end

    def refresh!(saved_value, attr_name, instance)
        values = intermediate_values(instance).reduce([]) { |result, entry|
            result << @type.find_by_id(entry[@type.name.to_sym]).first
            result
        }
        instance.send(attr_name.to_s + "=", values)
    end

    def intermediate_table(instance)
        TADB::DB.table(instance.class.name + @type.name)
    end

    def intermediate_values(instance)
        intermediate_table(instance).entries.select {|entry| entry[instance.class.name.to_sym] == instance.id}
    end

    def forget!(instance)
        ids = intermediate_values(instance).reduce([]) { |result, entry|
            result << entry[:id]
            result
        }
        ids.each do |id|
            intermediate_table(instance).delete(id)
        end
    end

    def all_instances(saved_value, instance)
        ids = intermediate_values(instance)
        ids.reduce([]) { |result, entry|
            result << @type.find_by_id(entry[@type.name.to_sym]).first
            result
        }
    end
end