require_relative 'spec_helper'

describe 'Herency and Composition' do

    before { TADB::DB.clear_all }
    after  { TADB::DB.clear_all }

    describe 'Composicion con unico objeto' do
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

    describe 'Composicion con varios objetos' do
        let(:s) { StudentWithManyGrades.new }

        before do
            s.full_name = "leo sbaraglia"
            s.ubicacion = Point.new
            s.ubicacion.x = 1
            s.ubicacion.y = 2
        end

        def add_grade(value)
            s.grades.push(Grade.new)
            s.grades.last.value = value
        end

        it "Inicializa el atributo del has_many en []" do
            expect(s.grades).to eq []
        end
        it "Agrego 2 grades, guardo, agrego una 3ra y al refreshear solo aparecen las guardadas" do
            self.add_grade(8)
            self.add_grade(5)
            s.save!
            self.add_grade(7)
            expect(s.refresh!.full_name).to eq "leo sbaraglia"
            expect(s.refresh!.ubicacion.x).to eq 1
            expect(s.refresh!.ubicacion.y).to eq 2
            expect(s.refresh!.grades[0].value).to eq 8
            expect(s.refresh!.grades[1].value).to eq 5
            expect(s.refresh!.grades[2]).to be_nil
        end
        it "Agrego 2 grades, guardo, cambio el value de una, lo guardo y al refreshear tiene el valor cambiado" do
            self.add_grade(8)
            self.add_grade(5)
            s.save!
            g = s.grades.last
            g.value = 6
            g.save!
            expect(s.refresh!.full_name).to eq "leo sbaraglia"
            expect(s.refresh!.ubicacion.x).to eq 1
            expect(s.refresh!.ubicacion.y).to eq 2
            expect(s.refresh!.grades[0].value).to eq 8
            expect(s.refresh!.grades[1].value).to eq 6
        end
    end

end
