require_relative 'spec_helper'

describe 'Persistent' do

  before { TADB::DB.clear_all }
  after  { TADB::DB.clear_all }

  describe '#save!' do

    describe 'Se guarda y te crea el id' do
      let(:raul) { Person.new }

      before do
        raul.first_name = 'raul'
        raul.last_name = 'porcheto'
        raul.age = 74
        raul.admin = false
        raul.save!
      end

      it do
        expect(raul.id).to_not be_nil
      end
    end

  end

  describe '#refresh!' do

    it 'Se hace refresh de un objeto no persistido' do
      expect { Person.new.refresh! }.to raise_error NotPersistedError
    end

    describe 'Se guarda, se modifica y se hace un refresh antes de guardar' do
      let(:raul) { Person.new }

      before do
        raul.first_name = 'raul'
        raul.last_name = 'porcheto'
        raul.admin = false
        raul.age = 74
        raul.save!
      end


      it do
        raul.first_name = 'pepe'
        expect(raul.first_name).to eql 'pepe'
        raul.refresh!
        expect(raul.first_name).to eql 'raul'
      end
    end
  end

  describe '#forget!' do

    let(:raul) { Person.new }

    before do
      raul.first_name = 'raul'
      raul.last_name = 'porcheto'
      raul.admin = false
      raul.age = 74
      raul.save!
    end

    it 'Se guarda la persona y se la olvida' do
      expect(raul.id).to_not be_nil
      raul.forget!
      expect(raul.id).to be_nil
    end
  end

end