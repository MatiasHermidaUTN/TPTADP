class AttrNotCorrectTypeError < StandardError
    attr_accessor :object
    def initialize(object)
        self.object = object
        super
    end
end