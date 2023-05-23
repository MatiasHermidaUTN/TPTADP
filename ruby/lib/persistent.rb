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
    table.delete(id) unless id.nil?
    self.class.persistent_attributes.each do |attr_name, db_type|
      hash_all_attr = db_type.save!(hash_all_attr, attr_name, self)
    end
    self.id = table.insert(hash_all_attr)
  end

  def refresh!
    raise NotPersistedError.new(self) if id.nil?
    saved_obj = table.entries.find {|entry| entry[:id] == self.id}
    self.class.persistent_attributes.each do |attr_name, db_type|
      saved_value = saved_obj[attr_name]
      db_type.refresh!(saved_value, attr_name, self)
    end
    self
  end

  def forget!
    # hay que borrar los objetos complejos que estan asociados a este objeto?
    table.delete(self.id)
    self.id = nil
  end

  def table
    TADB::DB.table(self.class.name)
  end

  def attributes_hash
    hash = {}
    self.class.persistent_attributes.keys.each do |attribute_name|
      hash.store(attribute_name, send(attribute_name))
    end
    hash
  end

end