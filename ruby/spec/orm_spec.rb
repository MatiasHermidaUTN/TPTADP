require_relative 'spec_helper'

describe 'ORM' do

  describe '#has_one' do

    describe 'pisa atributos con el mismo nombre' do
      it { expect(Grade.persistent_attributes[:value]).to eql Numeric }
    end

    describe 'no valida el tipo' do
      let(:raul) { Person.new }

      before do
        raul.first_name = 'raul'
        raul.last_name = 8
      end

      it { expect(raul.first_name).to eql 'raul' }
      it { expect(raul.last_name).to eql 8 }
    end
  end

end