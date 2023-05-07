require_relative 'spec_helper'

describe 'Herency and Composition' do

  describe 'Composicion con unico objeto' do
    it "algo" do
      s = StudentWithGrade.new
      s.full_name = "leo sbaraglia"
      s.grade = Grade.new
      s.grade.value = 8
      s.save!
      g = s.grade
      expect(g.value).to eq 8
      g.value = 5
      g.save!
      expect(s.refresh!.grade.value).to eq 5
    end
  end
end

