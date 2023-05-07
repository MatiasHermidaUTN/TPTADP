require 'errors/not_persisted_error'

module Persistent

  def save!
    if (id)
      table.delete(id)
    end
    self.id = table.insert(self.attributes_hash)
  end

  def refresh!
    if !id
      raise NotPersistedError.new(self)
    end
    entry = table.entries.select { |hash| hash[:id] == id }
    if entry.empty?
      raise NotPersistedError.new(self)
    end
    entry[0].each do |key, value|
      self.send("#{key}=", value)
    end
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