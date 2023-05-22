require_relative './errors/not_persisted_error'
require_relative './errors/AttrNotCorrectTypeError'
require_relative './errors/ValidationError'

module Persistent

  def validate!(hash_all_attr)
    self.class.persistent_attributes.each do |attr_name, db_type|
      if (attr_name != :id)
        hash_all_attr = db_type.validate!(hash_all_attr, attr_name)
      end
    end
  end

  def save!
    hash_all_attr = self.attributes_hash
    self.validate!(hash_all_attr)

    save_complex!(hash_all_attr)
    save_many!(hash_all_attr)

    table.delete(id) unless id.nil?
    self.id = table.insert(hash_all_attr)
  end

  def save_many!(hash_all_attr)
    hash_many_attr = hash_all_attr.select do |attr_name, value| value.is_a?(Array) end  # {full_names => ["Juan", "Pedro"], grades => [grade1, grade2]}
    hash_many_attr.each do |attr_name, array_values|  # attr_name = grades, array_values = [grade1, grade2]
      # if array_values.empty?
      #   all_attr_values.store(attr_name, 0)
      #   next
      # end
      i=0
      hash_values = array_values.to_h do |attr| ["#{attr_name}_#{i += 1}", attr] end  # {grades_1 => grade1, grades_2 => grade2}

      db_type = self.class.persistent_attributes[attr_name]
      if db_type.class == OneComplexDbType || db_type.class == ManyComplexDbType
        hash_values.each do |attr_name1, value|  # {grades_1 => grade1, grades_2 => grade2}
          id_complex = value.save!    #save! devuelve id de la tabla del attr complejo
          hash_values[attr_name1] = id_complex  # {grades_1 => id1, grades_2 => id2}
        end
      end

      id_many = TADB::DB.table(attr_name).insert(hash_values)
      hash_all_attr.store(attr_name, id_many)
    end
  end

  def save_complex!(hash_all_attr)
    self.get_complex_attributes.each do |attr_name, type|
      unless hash_all_attr[attr_name].is_a?(Array)
        id_complex = hash_all_attr[attr_name].save! #save! devuelve id de la tabla del attr complejo
        hash_all_attr[attr_name] = id_complex
      end
    end
  end

  def refresh!
    raise NotPersistedError.new(self) if id.nil?
    saved_obj = table.entries.find {|entry| entry[:id] == self.id}
    self.class.persistent_attributes.each do |attr_name, db_type|
      saved_value = saved_obj[attr_name]
      if self.get_has_many_attributes.include?(attr_name)
        self.refresh_many!(attr_name, db_type.type, saved_value)
      else
        value = db_type.class == OneComplexDbType || db_type.class == ManyComplexDbType ? db_type.type.find_by_id(saved_value).first : saved_value  #agrego el first pq find_by_id devuelve un array
        self.send(attr_name.to_s + "=", value)
      end
    end
    self
  end

  def refresh_many!(attr_name, type_attr, id_many)
    hash_attr = TADB::DB.table(attr_name).entries.select{ |entry| entry[:id] == id_many }.first
    self.send(attr_name.to_s + "=", [])
    hash_attr.keys.each do |key|
      if key == :id then next end
      value = self.class.is_complex_type?(type_attr) ? type_attr.find_by_id(hash_attr[key]).first : hash_attr[key]
      self.send(attr_name.to_s).push(value)
    end
  end

  def forget!
    # hay que borrar los objetos complejos que estan asociados a este objeto?
    table.delete(self.id)
    self.id = nil
  end

  def table
    TADB::DB.table(self.class.name)
  end

  def get_complex_attributes
    self.class.persistent_attributes.select do | attr_name, db_type|
      db_type.class == OneComplexDbType || db_type.class == ManyComplexDbType
    end
  end

  def get_has_many_attributes
    self.class.persistent_attributes.select do |attr_name, db_type|
      self.send(attr_name).is_a?(Array)
    end
  end

  def attributes_hash
    hash = {}
    self.class.persistent_attributes.keys.each do |attribute_name|
      hash.store(attribute_name, send(attribute_name))
    end
    hash
  end

end