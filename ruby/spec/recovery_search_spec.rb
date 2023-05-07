require_relative 'spec_helper'

describe 'Recovery and Search' do
  before { TADB::DB.clear_all }
  after  { TADB::DB.clear_all }

  def agregarEstudianteAprobado
    student = Student.new
    student.full_name = "tito puente"
    student.grade = 9
    student.id = "10"
    student.save!
  end
  def agregarEstudianteDesaprobado
    student = Student.new
    student.full_name = "tito puente"
    student.grade = 2
    student.id = "5"
    student.save!
  end
  describe '#all_instances' do
    it 'Se buscan las instancias de una clase que fue guardada' do
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

  describe '#find_by_<what>' do
    it "Busca por id" do
      agregarEstudianteDesaprobado
      agregarEstudianteAprobado
      students = Student.find_by_id("5")
      expect(students.length).to eq 1
      students.each do |student|
        expect(student.id).to eq "5"
      end
    end
    it "Busca por nombre" do
      agregarEstudianteDesaprobado
      agregarEstudianteAprobado
      students = Student.find_by_full_name("tito puente")
      expect(students.length).to eq 2
      students.each do |student|
        expect(student.full_name).to eq "tito puente"
      end
    end
    it "Busca por nota" do
      agregarEstudianteDesaprobado
      agregarEstudianteAprobado
      students = Student.find_by_grade(2)
      expect(students.length).to eq 1
      students.each do |student|
        expect(student.grade).to eq 2
      end
    end
    it "Busca a los que no promocionaron" do
      agregarEstudianteDesaprobado
      agregarEstudianteAprobado
      students = Student.find_by_promoted(false)
      expect(students.length).to eq 1
      students.each do |student|
        expect(student.promoted).to eq false
      end
    end
    it "Rompe ya que el mensaje porque has_last_name recibe args" do
      agregarEstudianteDesaprobado
      agregarEstudianteAprobado
      expect { Student.find_by_has_last_name("puente") }.to raise_error NoMethodError
    end
  end
end