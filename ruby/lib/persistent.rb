require 'errors/not_persisted_error'

module Persistent
  @@primitive_values = [Numeric, String, Boolean]

  def save!
    entry = {}
    self.class.persistent_attributes.each do |name, type|
      entry[name] = @@primitive_values.include?(type) ? send(name) : send(name).save!
    end
    if (id)
      table.delete(id)
    end
    self.id = table.insert(entry)
  end

  def refresh!
    if !id
      raise NotPersistedError.new(self)
    end
    entry = table.entries.select { |hash| hash[:id] == id }
    if entry.empty? # Por si se agrego el id a mano y nunca se guardo
      raise NotPersistedError.new(self)
    end
    entry = entry[0]
    self.class.persistent_attributes.each do |name, type|
      entry_value = entry[name]
      value = @@primitive_values.include?(type) ? entry_value : type.find_by_id(entry_value)[0]
      self.send("#{name}=", value)
    end
    self
  end

  def forget!
    table.delete(id)
    self.id = nil
  end

  def id
    @id
  end

  def id=(value)
    @id = value
  end

  private

  def table
    TADB::DB.table(self.class.name)
  end

  def attributes_hash
    hash = {}
    self.class.persistent_attributes.keys.each do |attribute_name|
      hash = hash.merge(attribute_name => send(attribute_name))
    end
    hash
  end

end

Object.include(Persistent)