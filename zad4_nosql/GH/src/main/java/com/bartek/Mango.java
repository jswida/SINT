package com.bartek;

import com.bartek.models.Course;
import com.bartek.models.Grade;
import com.bartek.models.Squence;
import com.bartek.models.Student;
import com.mongodb.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.List;

import static com.bartek.Storage.*;

public class Mango {
    private final Datastore datastore;
    private static Mango mangoIns = new Mango();

    public Mango() {
        MongoClient client = new MongoClient("localhost", 8004);
        Morphia morphia = new Morphia();
        morphia.mapPackage("models");

        this.datastore = morphia.createDatastore(client, "DATABASE");
        this.datastore.ensureIndexes();
        this.datastore.enableDocumentValidation();
        this.generateFirstMango(this);
    }

    public static Mango getMangoIns() {
        return mangoIns;
    }

    public static void setMangoIns(Mango mangoIns) {
        Mango.mangoIns = mangoIns;
    }

    public void generateFirstMango(Mango mango) {
        if (this.datastore.createQuery(Student.class).count() > 0
                && this.datastore.createQuery(Course.class).count() > 0
                && this.datastore.createQuery(Squence.class).count() > 0) {
            System.out.println("DB not empty");
        } else {

            Course course1 = generateCourse("QWER");
            Grade grade1 = generateGrade(course1);

            Course course2 = generateCourse("ASDF");
            Grade grade2 = generateGrade(course2);

            Course course3 = generateCourse("ZXCV");
            Grade grade3 = generateGrade(course3);

            Student student1 = generateStudent("Jerry", "Arry");
            Student student2 = generateStudent("Merry", "Berry");
            Student student3 = generateStudent("Terry", "Cerry");

            student1.setGrade(grade1);
            student2.setGrade(grade2);
            student3.setGrade(grade3);

            this.datastore.save(new Squence(123456L, 4L, 4L));

            this.datastore.save(course1);
            this.datastore.save(course2);
            this.datastore.save(course3);

            this.datastore.save(grade1);
            this.datastore.save(student1);

            this.datastore.save(grade2);
            this.datastore.save(student2);

            this.datastore.save(grade3);
            this.datastore.save(student3);
        }
    }

    // next

    private synchronized Long nextStudentId() {
        Squence id = datastore.findAndModify(
                datastore.createQuery(Squence.class),
                datastore.createUpdateOperations(Squence.class)
                        .inc("studentIndex", 1));
        return id.getStudentIndex();
    }

    private synchronized Long nextCourseId() {
        Squence id = datastore.findAndModify(
                datastore.createQuery(Squence.class),
                datastore.createUpdateOperations(Squence.class)
                        .inc("courseID", 1));
        return id.getCourseID();
    }

    private synchronized Long nextGradeId() {
        Squence id = datastore.findAndModify(
                datastore.createQuery(Squence.class),
                datastore.createUpdateOperations(Squence.class)
                        .inc("gradeID", 1));
        return id.getGradeID();
    }

    // get

    public Course getCourse(Long id) throws NotFoundException {
        Course course = datastore.find(Course.class).field("id").equal(id).get();
        if (course == null) throw  new NotFoundException();
        return course;
    }

    public Student getStudent(Long id) throws NotFoundException{
        Student student = datastore.find(Student.class).field("index").equal(id).get();
        if (student == null) throw new NotFoundException();
        return student;
    }

    public Grade getGrade(Student student, Long gradeId) throws NotFoundException {
        Grade grade = datastore.find(Grade.class).field("studentId").equal(student.getIndex()).field("id").equal(gradeId).get();
        if(grade == null){
            throw new NotFoundException();
        }
        return grade;
    }

    public List<Student> getStudents() {
        return datastore.find(Student.class).asList();
    }

    public List<Course> getCourses() {
        return datastore.find(Course.class).asList();
    }

    public List<Grade> getGrades(Long index) throws NotFoundException {
        Student student = this.getStudent(index);
        if(student != null){
            return datastore.createQuery(Grade.class).field("studentId").equal(student.getIndex()).asList();
        }
        throw new NotFoundException();
    }

    // delete

    public void deleteStudent(Student student) {
        datastore.delete(datastore.find(Grade.class).field("studentId").equal(student.getIndex()));
        datastore.delete(student);
    }

    public void deleteCourse(Course course){
        datastore.delete(datastore.find(Grade.class).field("course").equal(course));
        datastore.delete(course);
    }

    public void deleteGrade(Student student, Grade grade) {
        datastore.delete(grade);
        // added
        student.getGrades().remove(grade);
        datastore.save(student);
    }

    // post

    public Student addStudent(Student student) throws BadRequestException {
        if (student.getFirstName() != null && student.getLastName() != null && student.getBirthday() != null){
            Long index = this.nextStudentId();
            Student newStudent = new Student(
                    index, student.getFirstName(), student.getLastName(),
                    student.getBirthday(), new HashSet<>());
            datastore.save(newStudent);
            return this.getStudent(index);
        }
        throw new BadRequestException();
    }

    public Course addCourse(Course course) throws BadRequestException {
        if (course.getName() != null && course.getLecturer() != null){
            Long id = this.nextCourseId();
            Course newCourse = new Course(id, course.getName(), course.getLecturer());
            datastore.save(newCourse);
            return this.getCourse(id);
        }
        throw new BadRequestException();
    }

    public Grade addGrade(Grade ng) throws NotFoundException, BadRequestException {
        if (ng.getValue() != 0 && ng.getDate() != null && ng.getCourse().getId() != null) {
            Long id = this.nextGradeId();
            Grade grade = new Grade(id, ng.getValue(), ng.getDate(), ng.getCourse());
            grade.setStudentId(ng.getStudentId());

            System.out.println(grade);

            Student student = getStudent(ng.getStudentId());
            System.out.println("have student");
            // added
            student.getGrades().add(grade);

            datastore.save(grade);
            // added
            datastore.save(student);
            return grade;
        }
        throw new BadRequestException();
    }

    // put

    public Student updateStudent(Student student, Student ns) throws NotFoundException{
        if(ns.getBirthday() != null) {
            student.setBirthday(ns.getBirthday());
        }
        if(ns.getLastName() != null) {
            student.setSurname(ns.getLastName());
        }
        if(ns.getFirstName() != null) {
            student.setName(ns.getFirstName());
        }
        datastore.save(student);
        return this.getStudent(student.getIndex());
    }

    public Course updateCourse(Course course, Course nc) throws NotFoundException {
        if(nc.getLecturer() != null){
            course.setLecturer(nc.getLecturer());
        }
        if(nc.getName() != null){
            course.setName(nc.getName());
        }
        datastore.save(course);
        return this.getCourse(course.getId());
    }

    public Grade updateGrade(Student student, Grade grade, Grade ng) throws NotFoundException {
        if (grade != null) {
            if (ng.getValue() != 0) {
                grade.setValue(ng.getValue());
            }
            if (ng.getDate() != null && ng.getDate().toString().length() > 0) {
                grade.setDate(ng.getDate());
            }
            if (ng.getCourse().getId() != null && ng.getCourse().getId() >= 0){
                Course course = this.getCourse(ng.getCourse().getId());
                if (course != null) {
                    grade.setCourse(course);
                }
            }
            datastore.save(grade);
            datastore.save(student);
            return grade;

        } else throw new NotFoundException();
    }



}
