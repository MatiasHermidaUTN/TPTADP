require_relative 'spec_helper'

describe 'ORM' do

    before { TADB::DB.clear_all }
    after  { TADB::DB.clear_all }

    describe '#has_one' do

        it 'pisa atributos con el mismo nombre' do
            expect(Grade.persistent_attributes[:value]).to eql Numeric
        end

        describe 'no valida el tipo' do
            let(:raul) { Person.new }

            before do
                raul.first_name = 'raul'
                raul.last_name = 8
            end

            it do expect(raul.first_name).to eql 'raul' end
            it do expect(raul.last_name).to eql 8 end
        end
    end

    describe '#all_instances' do

        describe 'si no hay instancias' do
            it 'devuelve vacio' do expect(Person.all_instances).to be_empty end
        end

        describe 'hay instancias de objetos simples' do
            let(:p1) { Point.new }
            let(:p2) { Point.new }
            let(:p3) { Point.new }
            let(:p4) { Point.new }

            before do
                p1.x = 2
                p1.y = 5
                p1.save!
                p2.x = 1
                p2.y = 3
                p2.save!
                p3.x = 9
                p3.y = 7
            end

            it 'prueba integral all_instances' do
                all_instances = Point.all_instances
                expect(all_instances.length).to eq 2
                expect(all_instances[0].x).to eq 2
                expect(all_instances[0].y).to eq 5
                expect(all_instances[1].x).to eq 1
                expect(all_instances[1].y).to eq 3
                # expect(Point.all_instances).to match [p1, p2]
                p4 = Point.all_instances.first
                p4.add(p2)
                p4.save!
                all_instances = Point.all_instances
                expect(all_instances.length).to eq 2
                expect(all_instances[0].x).to eq 1
                expect(all_instances[0].y).to eq 3
                expect(all_instances[1].x).to eq 3
                expect(all_instances[1].y).to eq 8
                # expect(Point.all_instances).to match [p2, p4]
                p2.forget!
                all_instances = Point.all_instances
                expect(all_instances.length).to eq 1
                expect(all_instances[0].x).to eq 3
                expect(all_instances[0].y).to eq 8
                # expect(Point.all_instances).to match [p4]
            end
        end
    end

    describe '#find_by' do

        describe 'no hay instancias' do
            it do expect(Student.find_by_full_name('raul')).to be_empty end
        end

        describe 'hay instancias de objetos simples' do
            let(:s1) { Student.new }
            let(:s2) { Student.new }
            let(:s3) { Student.new }

            before do
                s1.full_name = 'tito puente'
                s1.grade = 5
                s1.save!
                s2.full_name = 'tito gonzales'
                s2.grade = 9
                s2.save!
                s3.full_name = 'mariana perez'
                s3.grade = 10
                s3.save!
            end

            it 'prueba integral find_by' do
                personas_match = Student.find_by_full_name('tito puente')
                expect(personas_match.first.full_name).to eq 'tito puente'
                expect(personas_match.first.grade).to eq 5

                personas_match = Student.find_by_grade(9)
                expect(personas_match.first.full_name).to eq 'tito gonzales'
                expect(personas_match.first.grade).to eq 9

                personas_match = Student.find_by_id(s3.id)
                expect(personas_match.first.full_name).to eq 'mariana perez'
                expect(personas_match.first.grade).to eq 10

                personas_match = Student.find_by_promoted(true)
                expect(personas_match[0].full_name).to eq 'tito gonzales'
                expect(personas_match[0].grade).to eq 9
                expect(personas_match[1].full_name).to eq 'mariana perez'
                expect(personas_match[1].grade).to eq 10

                expect{Student.find_by_has_last_name("puente")}.to raise_error NotValidMethodForFindByError

            end
        end
    end
end