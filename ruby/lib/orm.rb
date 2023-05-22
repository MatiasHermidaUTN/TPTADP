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

        self.add_persistent_attribute!({ attribute_name => type }, { attribute_name => options })
        # {name => atributo_persistible}
        # Y que atributo_persistible tenga type => tipo, validations => options

        include Persistent
        self.init_descendant_registration

        self.define_method(:initialize) do
            self.class.persistent_attr_validations.select{|attr_name, validations| validations&.has_key?(:default)}.each do |attr_name, validations|
                self.send(attr_name.to_s + "=", validations[:default])
            end
        end
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
        def self.descendants
            @descendants ||= []
        end
    end

    def has_many(type, description)
        self.has_one(type, description)
        self.attr_getter_has_many(description[:named])
    end

    def attr_getter_has_many(attribute_name)
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
                    type_attr = self.persistent_attr_types[attribute_name]
                    value = self.is_complex_type?(type_attr) ? type_attr.find_by_id(saved_value).first : saved_value  #agrego el first pq find_by_id devuelve un array
                    instance.send(attribute_name.to_s + "=", value)
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
        if responds_to_find_by?(symbol)
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

    def persistent_attr_types
        @persistent_attr_types ||= {:id => String}
        self.ancestors.drop(1).flat_map { |ancestor| ancestor.persistent_attr_types }.inject(@persistent_attr_types) { |result, ancestor| result.merge(ancestor) }
    end

    def persistent_attr_validations
        @persistent_attr_validations ||= {}
        self.ancestors.drop(1).flat_map { |ancestor| ancestor.persistent_attr_validations }.inject(@persistent_attr_validations) { |result, ancestor| result.merge(ancestor) }
    end

    # private
    def add_persistent_attribute!(persistent_attr_type, persistent_attr_validation = {})
        @persistent_attr_types = self.persistent_attr_types.merge(persistent_attr_type)
        @persistent_attr_validations = self.persistent_attr_validations.merge(persistent_attr_validation)
    end

end
Module.include(ORM)
