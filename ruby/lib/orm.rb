require 'tadb'
require_relative './boolean'
require_relative './persistent'
require_relative './errors/not_valid_method_for_find_by_error'
require_relative './dbtype'
module ORM
    def has_db(db_type)
        attr_accessor db_type.name, :id
        self.add_persistent_attribute!(db_type.name => db_type)
        self.init_descendant_registration
        include Persistent

        define_method(:initialize) do
            self.class.persistent_attributes.each do |attr_name, db_type|
                if attr_name != :id && !db_type.default.nil?
                    send(attr_name.to_s + "=", db_type.default)
                end
            end
            super()
        end
    end

    def init_descendant_registration
        define_singleton_method(:descendants) do
            @descendants ||= []
        end
        define_singleton_method(:included) { |descendant|
            self.descendants.push(descendant)
        }
        define_singleton_method(:inherited) { |descendant|
            self.descendants.push(descendant)
        }
    end

    def has_one(type, description)
        self.has_db(is_complex_type?(type) ? OneComplexDbType.new(type, description) : OneSimpleDbType.new(type, description))
    end

    def has_many(type, description)
        self.has_db(is_complex_type?(type) ? ManyComplexDbType.new(type, description) : ManySimpleDbType.new(type, description))
        self.attr_reader_has_many(description[:named])
    end

    def attr_reader_has_many(attribute_name)
        define_method(attribute_name.to_s) do
            if self.instance_variable_get("@" + attribute_name.to_s).nil?
                self.instance_variable_set("@" + attribute_name.to_s, [])
            else
                self.instance_variable_get("@" + attribute_name.to_s)
            end
        end
    end

    def all_instances
        all_instances = []
        if self.is_a?(Class)
            all_instances += self.new.table.entries.collect do |entry|
                instance = self.new
                self.persistent_attributes.each do |attr_name, db_type|
                    saved_value = db_type.all_instances(entry[attr_name], instance)
                    instance.send(attr_name.to_s + "=", saved_value)
                end
                instance
            end
        end
        all_instances + self.descendants.flat_map { |descendant| descendant.all_instances }
    end


    def responds_to_find_by?(symbol)
        symbol.to_s.start_with?("find_by_")
    end

    def method_missing(symbol, *args, &block)
        if self.responds_to_find_by?(symbol)
            value = args[0]
            message = symbol.to_s.delete_prefix("find_by_")
            raise NotValidMethodForFindByError.new(self) if self.instance_method(message).arity > 0
            self.all_instances.select do |obj|
                obj.send(message) == value
            end
        else
            super
        end
    end

    def respond_to_missing?(symbol, include_private = false)
        self.responds_to_find_by?(symbol) || super
    end

    def is_complex_type?(type)
        ![Numeric, String, Boolean].include?(type)
    end

    def persistent_attributes
        id_db_type = OneSimpleDbType.new(String, { named: :id })
        @persistent_attributes ||= {id: id_db_type}
        self.ancestors.drop(1).reverse.flat_map { |ancestor| ancestor.persistent_attributes }.inject({}) { |result, ancestor| result.merge(ancestor) }.merge(@persistent_attributes)
    end

    # private
    def add_persistent_attribute!(persistent_attribute)
        @persistent_attributes = self.persistent_attributes.merge(persistent_attribute)
    end
end
Module.include(ORM)
