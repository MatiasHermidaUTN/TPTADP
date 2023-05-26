require_relative './errors/ValidationError'
class NoBlankValidation
    def validate!(value)
        raise ValidationError.new(self) if value.nil? or value == ""
    end
end

class FromValidation
    def initialize(value)
        @value = value
    end
    def validate!(value)
        raise ValidationError.new(self) unless value >= @value
    end
end

class ToValidation
    def initialize(value)
        @value = value
    end
    def validate!(value)
        raise ValidationError.new(self) unless value <= @value = value
    end
end

class ValidateValidation
    def initialize(validate)
        @validate = validate
    end
    def validate!(value)
        raise ValidationError.new(self) unless value.instance_exec(&@validate)
    end
end