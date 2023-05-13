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
        
    end

end