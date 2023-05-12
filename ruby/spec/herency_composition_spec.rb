require_relative 'spec_helper'

describe 'Herency and Composition' do

    before { TADB::DB.clear_all }
    after  { TADB::DB.clear_all }

    describe 'Se guarda composicion con unico objeto' do
        let(:s) { StudentWithGrade.new }

        before do
            s.full_name = "leo sbaraglia"
            s.grade = Grade.new
            s.grade.value = 8
            s.save!
        end

        it "Salva al estudiante y su nota" do
            expect(StudentWithGrade.find_by_id(s.id).first.grade.value).to eq 8
        end
        it "Cambia el value de grade, lo guardo y el refresh de s lo trae" do
            g = s.grade
            g.value = 5
            g.save!
            expect(s.refresh!.grade.value).to eq 5
        end
    end

    describe 'Se guarda composicion con varios objetos' do
        let(:s) { StudentWithManyGrades.new }

        before do
            s.full_name = "leo sbaraglia"
        end

        it "Inicializa el atributo del has_many en []" do
            expect(s.grades).to eq []
        end
        it "Agrego 2 grades con diferentes valores y se guardan todas" do
            # rompe acá
            s.grades.push(Grade.new)
            s.grades.last.value = 8
            s.grades.push(Grade.new)
            s.grades.last.value = 5
            # s.save!
            expect(s.refresh!.grades[0].value).to eq 8
            expect(s.refresh!.grades[1].value).to eq 5
        end
    end

end
