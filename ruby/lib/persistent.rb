require_relative './errors/not_persisted_error'

module Persistent

  def save!
    all_attr_values = self.attributes_hash

    self.get_complex_attributes.each do |attr_name, type|
      id_complex = all_attr_values[attr_name].save!  #save! devuelve id de la tabla del attr complejo
      all_attr_values[attr_name] = id_complex
    end

    table.delete(id) unless id.nil?
    self.id = table.insert(all_attr_values)
  end

  def refresh!
    raise NotPersistedError.new(self) if id.nil?
    saved_obj = table.entries.find {|entry| entry[:id] == self.id}
    self.class.persistent_attributes.each do |attr_name, type_attr|
      saved_value = saved_obj[attr_name]
      value = self.class.is_complex_type?(type_attr) ? type_attr.find_by_id(saved_value).first : saved_value  #agrego el first pq find_by_id devuelve un array
      self.send(attr_name.to_s + "=", value)
    end
    self
  end

  def forget!
    # hay que borrar los objetos complejos que estan asociados a este objeto?
    table.delete(self.id)
    self.id = nil
  end

  # private
  def table
    self.class.table
  end

  def get_complex_attributes
    self.class.persistent_attributes.select do |attr_name, type|
      self.class.is_complex_type?(type)
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