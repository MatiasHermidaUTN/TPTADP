require 'tadb'
require_relative './boolean'
require_relative './persistent'
require_relative './errors/not_valid_method_for_find_by_error'

module ORM

    def has_one(type, description)
        attribute_name = description[:named]
        attr_accessor attribute_name, :id
        if persistent_attributes.nil? then @persistent_attributes = {id: String} end
        add_persistent_attribute!(attribute_name => type)
        if self.is_a?(Class) then include Persistent end
    end

    def has_many(type, description)
        self.has_one(type, description)
        self.define_method("#{description[:named]}") do
            self.instance_variable_get("@#{description[:named]}").nil? ? [] : self.instance_variable_get("@#{description[:named]}")
        end
    end

    def all_instances
        # opcion 1 - solo para tipos simples:
        # table.entries.collect do |entry|
        #     instance = self.new
        #     entry.each do |attribute_name, saved_value|     #este saved_value puede ser un id y con el codigo sig no se recupera el objeto
        #         instance.send(attribute_name.to_s + "=", saved_value)
        #     end
        #     instance
        # end
        # opcion 2 - si tengo tipos complejos, busco sus objetos en la tabla:
        table.entries.collect do |entry|
            instance = self.new
            entry.each do |attribute_name, saved_value|
                type_attr = self.persistent_attributes[attribute_name]
                value = self.is_complex_type?(type_attr) ? type_attr.find_by_id(saved_value).first : saved_value  #agrego el first pq find_by_id devuelve un array
                instance.send(attribute_name.to_s + "=", value)
            end
            instance
        end
    end


    def responds_to_find_by(symbol)
        symbol.to_s.start_with?("find_by_")
    end

    def method_missing(symbol, *args, &block)
        if responds_to_find_by(symbol)
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
        responds_to_find_by(symbol) || super
    end

    def table
        TADB::DB.table(self.name)
    end

    def is_complex_type?(type)
        ![Numeric, String, Boolean].include?(type)
    end

    def persistent_attributes
        @persistent_attributes
    end

    # private
    def add_persistent_attribute!(persistent_attribute)
        @persistent_attributes = persistent_attributes.merge(persistent_attribute)
    end

end
Module.include(ORM)
