require_relative 'spec_helper'

describe 'Prueba' do

  let(:prueba) { Prueba.new }

  describe '#materia' do

    it 'deberia pasar este test' do
      expect(prueba.materia).to be :tadp
    end

  end

end