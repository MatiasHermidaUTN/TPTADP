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

end