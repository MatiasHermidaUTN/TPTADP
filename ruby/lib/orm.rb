require 'tadb'
require_relative './boolean'
require_relative './persistent'
require_relative './errors/not_valid_method_for_find_by_error'

module ORM

    def has_one(type, description)
        attribute_name = description[:named]
        attr_accessor attribute_name, :id
        options = {:no_blank => description[:no_blank], :from => description[:from], :to => description[:to],
                                 :validate => description[:validate], :default => description[:default]}.compact
        self.add_persistent_attribute!(attribute_name => {:type => type, :validations => options})
        if self.is_a?(Class) then include Persistent end
        self.init_descendant_registration
    end

    def init_descendant_registration
        def self.included(descendant)
            @descendants ||= []
            @descendants << descendant
        end
        def self.inherited(descendant)
            @descendants ||= []
            @descendants << descendant
        end
        def descendants
            @descendants ||= []
        end
    end

    def has_many(type, description)
        self.has_one(type, description)
        self.attr_accessor_has_many(description[:named])
    end

    def attr_accessor_has_many(attribute_name)
        self.define_method(attribute_name.to_s) do
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
                entry.each do |attribute_name, saved_value|
                    type_attr = self.persistent_attributes_types[attribute_name]
                    value = self.is_complex_type?(type_attr) ? type_attr.find_by_id(saved_value).first : saved_value  #agrego el first pq find_by_id devuelve un array
                    instance.send(attribute_name.to_s + "=", value)
                end
                instance
            end
        end
        self.descendants.sum(all_instances) { |descendant| descendant.all_instances }
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
        self.responds_to_find_by(symbol) || super
    end

    def is_complex_type?(type)
        ![Numeric, String, Boolean].include?(type)
    end

    def persistent_attributes
        first_ancestor = self.ancestors[1]
        @persistent_attributes ||= {id: {:type => String}}.merge(first_ancestor.respond_to?(:persistent_attributes) ? first_ancestor.persistent_attributes : {} )
    end

    def persistent_attributes_types
        self.persistent_attributes.transform_values do |value|
            value[:type]
        end
    end

    def persistent_attributes_validations
        self.persistent_attributes.transform_values do |value|
            value[:validations]
        end
    end

    # private
    def add_persistent_attribute!(persistent_attribute)
        @persistent_attributes = self.persistent_attributes.merge(persistent_attribute)
    end

end
Module.include(ORM)
