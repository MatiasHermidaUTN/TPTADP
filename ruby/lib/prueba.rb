require 'tadb'
require 'pry'

TADB::DB.clear_all
TABLA = TADB::DB.table('TESTING', true)
module ORM
  def has_one(tipo, options = {})
    puts "Llegue a ORM de class"
    # self.send(:attr_accessor, options[:named])
    # self.attr_accessor :id
    # self.attr_accessor :tabla
    # @attr_persistibles[options[:named]] = tipo
    @tabla = TADB::DB.table(self.name)
    puts @tabla

    # if(self.method.contains(:save!).not)
      self.define_method(:save!) do
        aux = self.instance_variable_get(("@"+options[:named].to_s).to_sym)
        @id = TABLA.insert({options[:named] => aux, options[:named] => aux})
      end
    # end

    self.send(:define_method, :refresh!, proc {}) # guardar en tabla
    puts self
    # super
    super
  end
end
module ORMA
  def has_one(tipo, options = {})
    puts "Llegue a ORMA de module"
    self.send(:attr_accessor, options[:named])
    self.attr_accessor :id
    self.attr_accessor :tabla
    # @attr_persistibles[options[:named]] = tipo
    puts self
  end
end

class Module
  include ORMA
end
class Class
  include ORM
end

module A
  has_one String, named: :nombre
end
class Prueba
  include A
  has_one String, named: :first_name
  has_one String, named: :last_name
  def materia
    :tadp
  end
end

# puts p = Prueba.new
# puts p.first_name = "hola"
# puts p.last_name = "CHAU"
# puts p.save!
# puts TABLA.entries

# METODOS DE TABLAS
# tabla = TADB::DB.table('blah')
# id = tabla.insert({subject: 'tadp'})
# entries = tabla.entries
# tabla.delete(id)
# tabla.clear
# TADB::DB.clear_all

# FIND_BY_IS_SUBJECT()
# tabla.entries.select do |elem|
# elem[:subject]
# end
# FIND_BY_ID("____")
# tabla.entries.select do |elem|
# elem[:id] == "66ea08ee-d6ea-4506-bd0d-e73072dbf139"
# end