require_relative 'spec_helper'

describe 'Validaciones y Defaults' do

    before { TADB::DB.clear_all }
    after  { TADB::DB.clear_all }

    describe "Validaciones de tipo" do
        let(:s) { StudentWithGrade.new }

        before do
            s.full_name = 5
            s.grade = Grade.new
            s.grade.value = 8
        end

        it "No permite guardar un atributo propio con un tipo incorrecto" do
            expect { s.save! }.to raise_error(AttrNotCorrectTypeError)
        end
        it "No permite guardar un atributo propio con un tipo incorrecto" do
            s.full_name = "pepe botella"
            expect(s.save!.class).to be String
        end
        it "No permite guardar un atributo complejo con un tipo incorrecto" do
            s.full_name = "pepe botella"
            s.grade.value = "nota"
            expect { s.save! }.to raise_error(AttrNotCorrectTypeError)
        end
    end

    describe "Validaciones de contenido" do
        let(:s) { StudentWithValidations.new }

        before do
            s.full_name = ""
        end

        it "Falla save si full_name vacio con no_blank true" do
            expect { s.save! }.to raise_error(ValidationError)
        end
        it "Falla save si age menor a from 18" do
            s.full_name = "emanuel ortega"
            s.age = 15
            expect { s.save! }.to raise_error(ValidationError)
        end
        it "Falla save si grade.value no cumple proc {value > 2}" do
            s.age = 22
            s.grades.push(Grade.new)
            expect { s.save! }.to raise_error(ValidationError)
        end
    end

    describe "Valores por defecto" do
        let(:s) { StudentWithDefault.new }

        before do
            s.grade.value = 8
        end

        it "full_name se inicializa al default" do
            expect(s.full_name).to eq "natalia natalia"
        end
        it "Al guardar full_name en nil, se guarda el valor por default" do
            s.full_name = nil
            s.save!
            s.refresh!
            expect(s.full_name).to eq "natalia natalia"
        end
    end

end