module Boolean
end

class TrueClass
    include Boolean
    def matches?(value)
        value == true
    end
end
class FalseClass
    include Boolean
    def matches?(value)
        value == false
    end
end
