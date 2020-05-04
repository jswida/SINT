package com.asia.models;

import com.asia.DataBase;
import com.mongodb.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.*;


public class Model {
    private final MongoClient mongoClinet;
    private final Morphia morphia;
    private final Datastore datastore;
    private static Model instance = new Model();
    //private List<Course> courses;
    //private List<Student> students;

    public Datastore getDatastore() {return datastore ;}

    public Model() {
        this.mongoClinet = new MongoClient("localhost", 8004);
        this.morphia = new Morphia();
        this.morphia.mapPackage("models");
        this.datastore = this.morphia.createDatastore(mongoClinet, "DATABASE");
        this.datastore.ensureIndexes();
        this.datastore.enableDocumentValidation();
        this.fillDummy(this);
    }

    public static Model getInstance() {
        return instance;
    }

    public void fillDummy(Model model){
        if(this.datastore.createQuery(Student.class).count() > 0
            || this.datastore.createQuery(Course.class).count() > 0
            || this.datastore.createQuery(Seq.class).count() > 0)
            return;

        Course course1 = DataBase.generateCourse("SINT");
        Grade grade1 = DataBase.generateGrade(course1);

        Course course2 = DataBase.generateCourse("AEM");
        Grade grade2 = DataBase.generateGrade(course2);

        Course course3 = DataBase.generateCourse("PIRO");
        Grade grade3 = DataBase.generateGrade(course3);

        Student student1 = DataBase.generateStudent("Joanna", "Swida");
        Student student2 = DataBase.generateStudent("Michał", "Grazikowski");
        Student student3 = DataBase.generateStudent("Stanisław", "Szatniak");


        student1.setGrade(grade1);
        student2.setGrade(grade2);
        student3.setGrade(grade3);


//        Course course1 = new Course(0L,"TP", "T Pawlak");
//        Course course2 = new Course(1L,"SI", "T Pawlak");
//
//        Grade grade1 = new Grade(1L, 3.5, new Date(2020, 02, 17), course1);
//        Grade grade2 = new Grade(2L, 4.5, new Date(2020, 02, 17), course2);
//        Grade grade3 = new Grade(3L, 4.5, new Date(2020, 02, 17), course2);
//
//        Student student1 = new Student(1222L, "Joanna", "Swida", new Date(1997, 03, 17), null);
//        Student student2 = new Student(1223L, "Joanna", "Swida", new Date(1997, 03, 17), null);
//        Student student3 = new Student(1224L, "Joanna", "Swida", new Date(1997, 03, 17), null);

        student1.setGrade(grade1);
        student2.setGrade(grade2);
        student3.setGrade(grade3);

        this.datastore.save(course1);
        this.datastore.save(course2);

        this.datastore.save(grade1);
        this.datastore.save(grade2);
        this.datastore.save(grade3);

        this.datastore.save(student1);
        this.datastore.save(student2);
        this.datastore.save(student3);

    }

    private Long nextStudentId() {
        Seq id = datastore.findAndModify(datastore.createQuery(Seq.class), datastore.createUpdateOperations(Seq.class).inc("studentIndex", 1));
        return id.getStudentIndex();
    }

    private Long nextCourseId() {
        Seq id = datastore.findAndModify(datastore.createQuery(Seq.class), datastore.createUpdateOperations(Seq.class).inc("courseID", 1));
        return id.getCourseID();
    }

    private Long nextGradeId() {
        Seq id = datastore.findAndModify(datastore.createQuery(Seq.class), datastore.createUpdateOperations(Seq.class).inc("gradeID", 1));
        return id.getGradeID();
    }

    public List<Student> getStudents() {
        return datastore.createQuery(Student.class).asList();
    }

    public List<Course> getCourses() {
        Query<Course> query = datastore.createQuery(Course.class);
        return query.asList();
    }

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

    public Student addStudent(Student student) throws BadRequestException{
        if (student.getFirstName() != null && student.getLastName() != null && student.getBirthday() != null){
            Long index = this.nextStudentId();
            Student student1 = new Student(index, student.getFirstName(), student.getLastName(), student.getBirthday(), new HashSet<>());
            datastore.save(student1);
            return this.getStudent(index);
        }
        throw new BadRequestException();
    }

    public Course addCourse(Course course) throws BadRequestException {
        if (course.getName() != null && course.getLecturer() != null){
            Long id = this.nextCourseId();
            Course course1 = new Course(id, course.getName(), course.getLecturer());
            datastore.save(course1);
            return this.getCourse(id);
        }
        throw new BadRequestException();
    }

    public Course updateCourse(Course course, Course tmp) throws NotFoundException {
        if(tmp.getLecturer() != null){
            course.setLecturer(tmp.getLecturer());
        }
        if(tmp.getName() != null){
            course.setName(tmp.getName());
        }
        datastore.save(course);
        return this.getCourse(course.getId());
    }

    public void deleteCourse(Course course){
        datastore.delete(datastore.find(Grade.class).field("course").equal(course));
        datastore.delete(course);
    }

    public Student updateStudent(Student student, Student tmp) throws NotFoundException{
        if(tmp.getBirthday() != null) {
            student.setBirthday(tmp.getBirthday());
        }
        if(tmp.getLastName() != null) {
            student.setSurname(tmp.getLastName());
        }
        if(tmp.getFirstName() != null) {
            student.setName(tmp.getFirstName());
        }
        datastore.save(student);
        return this.getStudent(student.getIndex());
    }

    public void deleteStudent(Student student) {
        datastore.delete(datastore.find(Grade.class).field("studentIndex").equal(student.getIndex()));
        datastore.delete(student);
    }

    public List<Grade> getGrades(Long index) throws NotFoundException {
        Student student = this.getStudent(index);
        if(student != null){
            return datastore.createQuery(Grade.class).field("studentIndex").equal(student.getIndex()).asList();
        }
        throw new NotFoundException();
    }

    public Grade addGrade(Student student, Course course, Grade grade) throws NotFoundException, BadRequestException {
        if (grade.getDate() != null && grade.getValue() > 0){
            Long id = this.nextGradeId();
            Grade grade1 = new Grade(id, grade.getValue(), grade.getDate(), grade.getCourse());
            grade1.setStudentIndex(grade.getStudentIndex());
            datastore.save(grade1);
            return this.getGrade(student, id);
        }
        throw new BadRequestException();
    }

    public Grade updateGrade(Student student, Grade grade, Grade tmp) throws NotFoundException {
        if(tmp.getValue() > 0){
            grade.setValue(tmp.getValue());
        }
        if(tmp.getDate() != null){
            grade.setDate(tmp.getDate());
        }
        grade.setCourse(tmp.getCourse());
        datastore.save(grade);
        return this.getGrade(student, grade.getId());
    }

    public void deleteGrade(Student student, Grade grade) {
        datastore.delete(grade);
    }

    public Grade getGrade(Student student, Long gradeId) throws NotFoundException {
        Grade grade = datastore.find(Grade.class).field("studentIndex").equal(student.getIndex()).field("id").equal(gradeId).get();
        if(grade == null){
            throw new NotFoundException();
        }
        return grade;
    }

}
