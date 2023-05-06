module Persistent

  def save!
    self.id = table.insert(self.attributes_hash)
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