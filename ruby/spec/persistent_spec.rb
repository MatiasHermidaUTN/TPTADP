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
        raul.admin = false
        raul.age = 74
        raul.save!
      end

      it { expect(raul.id).to_not be_nil }
    end
  end

  describe '#refresh!' do
    it 'Se hace refresh de un objeto no persistido' do
      expect { Person.new.refresh! }.to raise_error NotPersistedError
    end
    it 'Se guarda, se modifica y se hace un refresh antes de guardar' do
      raul = Person.new
      raul.first_name = 'raul'
      raul.last_name = 'porcheto'
      raul.admin = false
      raul.age = 74
      raul.save!

      expect(raul.id).to_not be_nil

      raul.first_name = 'pepe'
      expect(raul.first_name).to eql 'pepe'

      raul.refresh!
      expect(raul.first_name).to eql 'raul'
    end
  end

  describe '#forget!' do
    it 'Se guarda la persona y se la olvida' do
      raul = Person.new
      raul.first_name = 'raul'
      raul.last_name = 'porcheto'
      raul.admin = false
      raul.age = 74
      raul.save!
      expect(raul.id).to_not be_nil

      raul.forget!
      expect(raul.id).to be_nil
    end
  end

  describe '#forget!' do
    it 'Se guarda la persona y se la olvida' do
      raul = Person.new
      raul.first_name = 'raul'
      raul.last_name = 'porcheto'
      raul.admin = false
      raul.age = 74
      raul.save!
      expect(raul.id).to_not be_nil

      raul.forget!
      expect(raul.id).to be_nil
    end
  end

  describe '#all_instances' do
    it 'Se guarda la persona y se la olvida' do
      p1 = Point.new
      p1.x = 2
      p1.y = 5
      p1.save!
      p2 = Point.new
      p2.x = 1
      p2.y = 3
      p2.save!

      p3 = Point.new
      p3.x = 9
      p3.y = 7

      all_instances = Point.all_instances
      expect(all_instances.length).to eq 2
      expect(all_instances[0].x).to eq 2
      expect(all_instances[0].y).to eq 5
      expect(all_instances[1].x).to eq 1
      expect(all_instances[1].y).to eq 3

      p4 = Point.all_instances.first
      p4.add(p2)
      p4.save!
      all_instances = Point.all_instances
      expect(all_instances.length).to eq 2
      expect(all_instances[0].x).to eq 1
      expect(all_instances[0].y).to eq 3
      expect(all_instances[1].x).to eq 3
      expect(all_instances[1].y).to eq 8

      p2.forget!
      all_instances = Point.all_instances
      expect(all_instances.length).to eq 1
      expect(all_instances[0].x).to eq 3
      expect(all_instances[0].y).to eq 8
    end
  end
end