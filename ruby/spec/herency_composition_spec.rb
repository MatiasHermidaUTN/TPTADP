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
        it "Agrego 2 grades, guardo y los borro" do
            self.add_grade(8)
            self.add_grade(5)
            s.save!
            s.forget!
            expect(s.id).to be_nil
        end
    end

    describe 'Herencia entre tipos' do
        let(:s1) { StudentWithPersonMixin.new }
        let(:assisProf) { AssistantProfessor.new }

        before do
            s1.full_name = "Pedro Gonzales"
            s1.grade = Grade.new
            s1.grade.value = 5
            s1.save!
            assisProf.full_name = "Miguel Sanchez"
            assisProf.grade = Grade.new
            assisProf.grade.value = 7
            assisProf.type = "Assistant"
            assisProf.save!
        end

        it "No existe una tabla para el modulo PersonMixin" do
            expect{PersonMixin.new.table}.to raise_error NoMethodError
        end
        it "Existe tabla para clase StudentWithPersonMixin con attr suyos mas de Persona" do
            expect{StudentWithPersonMixin.new.table}.to_not raise_error NoMethodError
            expect(StudentWithPersonMixin.persistent_attributes.keys).to eq [:id, :full_name, :grade]
        end
        it "Existe tabla para clase AssistantProfessor con attr suyos mas de StudentWithPersonMixin" do
            expect{AssistantProfessor.new.table}.to_not raise_error NoMethodError
            expect(AssistantProfessor.persistent_attributes.keys).to eq [:id, :full_name, :grade, :type]
        end

        it "Obtener todas las instancias del modulo PersonMixin trae las instancias de sus clases descendientes" do
            # puts PersonMixin.all_instances.inspect
            expect(StudentWithPersonMixin.all_instances[0].full_name).to eq s1.full_name
            expect(StudentWithPersonMixin.all_instances[0].grade.value).to eq s1.grade.value
            expect(StudentWithPersonMixin.all_instances[1].full_name).to eq assisProf.full_name
            expect(StudentWithPersonMixin.all_instances[1].grade.value).to eq assisProf.grade.value
            expect(StudentWithPersonMixin.all_instances[1].type).to eq assisProf.type
        end
        it "Obtener todas las instancias de la clase StudentWithPersonMixin trae sus instancias y las de sus clases descendientes" do
            # puts StudentWithPersonMixin.all_instances.inspect
            expect(StudentWithPersonMixin.all_instances[0].full_name).to eq s1.full_name
            expect(StudentWithPersonMixin.all_instances[0].grade.value).to eq s1.grade.value
            expect(StudentWithPersonMixin.all_instances[1].full_name).to eq assisProf.full_name
            expect(StudentWithPersonMixin.all_instances[1].grade.value).to eq assisProf.grade.value
            expect(StudentWithPersonMixin.all_instances[1].type).to eq assisProf.type
        end
        it "Obtener todas las instancias de la clase AssistantProfessor trae sus instancias" do
            # puts AssistantProfessor.all_instances.inspect
            expect(AssistantProfessor.all_instances[0].full_name).to eq assisProf.full_name
            expect(AssistantProfessor.all_instances[0].grade.value).to eq assisProf.grade.value
            expect(AssistantProfessor.all_instances[0].type).to eq assisProf.type
        end

        it "Find by full_name en StudentWithPersonMixin trae Estudiantes y Ayudantes con ese full_name" do
            ap2 = AssistantProfessor.new
            ap2.full_name = "Pedro Gonzales"
            ap2.grade = Grade.new
            ap2.grade.value = 10
            ap2.type = "Assistant"
            ap2.save!
            encontrados = StudentWithPersonMixin.find_by_full_name("Pedro Gonzales")
            expect(encontrados[0].full_name).to eq s1.full_name
            expect(encontrados[0].grade.value).to eq s1.grade.value
            expect(encontrados[1].full_name).to eq ap2.full_name
            expect(encontrados[1].grade.value).to eq ap2.grade.value
            expect(encontrados[1].type).to eq ap2.type
        end
        it "Find by type en StudentWithPersonMixin falla porque no todos Estudiantes y Ayudantes entienden el msj type" do
            ap2 = AssistantProfessor.new
            ap2.full_name = "Pedro Gonzales"
            ap2.grade = Grade.new
            ap2.grade.value = 10
            ap2.type = "Assistant"
            ap2.save!
            expect{StudentWithPersonMixin.find_by_type("Assistant")}.to_not raise_error NotValidMethodForFindByError
        end
    end

end
