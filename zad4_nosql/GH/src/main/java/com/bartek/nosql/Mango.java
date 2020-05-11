package com.bartek.nosql;

import com.bartek.models.Course;
import com.bartek.models.Grade;
import com.bartek.models.Student;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bartek.Storage.*;


public class Mango {
    private static final boolean debug = false;


    private static Mango instance = new Mango();
    private final Datastore datastore;

    private Mango() {
        final Morphia morphia = new Morphia();
        morphia.mapPackage("models");

        datastore = morphia.createDatastore(new MongoClient("localhost", 8004), "sint_db");
        datastore.enableDocumentValidation();
        datastore.ensureIndexes();

        this.loadMango();
        System.out.println("MongoDB loaded");
    }

    public static Mango getInstance() {
        return instance;
    }

    public void printCurrentStatus() {
        System.out.println("CURRENT DB STATE");
        System.out.println("Students \t Courses \t Grades");
        System.out.print(datastore.getCount(Student.class));
        System.out.print("\t\t\t");
        System.out.print(datastore.getCount(Course.class));
        System.out.print("\t\t\t");
        System.out.println(datastore.getCount(Grade.class));
    }

    public void loadMango() {
        if (debug) {
            datastore.delete(datastore.createQuery(Course.class));
            datastore.delete(datastore.createQuery(Student.class));
            datastore.delete(datastore.createQuery(Grade.class));
            datastore.delete(datastore.createQuery(Seq.class));
        }

        this.printCurrentStatus();

        if (datastore.getCount(Course.class) <= 0 && datastore.getCount(Student.class) <= 0) {
            datastore.save(new Seq(30000L, 2000L, 100L));
            try {
                Course course1 = generateCourse("QWER");
                course1.setId(nextCourseId());

                Course course2 = generateCourse("ASDF");
                course2.setId(nextCourseId());

                Course course3 = generateCourse("ZXCV");
                course3.setId(nextCourseId());

                Student student1 = generateStudent("Jerry", "Arry");
                student1.setIndex(nextStudentId());
                Student student2 = generateStudent("Merry", "Berry");
                student2.setIndex(nextStudentId());
                Student student3 = generateStudent("Terry", "Cerry");
                student3.setIndex(nextStudentId());

                this.datastore.save(course1);
                this.datastore.save(course2);
                this.datastore.save(course3);

                this.datastore.save(student1);
                this.datastore.save(student2);
                this.datastore.save(student3);

                Grade grade1 = generateGrade(getCourseByID(course1.getId()));
                grade1.setId(nextGradeId());
                grade1.setStudent(getStudentByID(student1.getIndex()));
                grade1.setStudentId(student1.getIndex());

                Grade grade2 = generateGrade(getCourseByID(course2.getId()));
                grade2.setId(nextGradeId());
                grade2.setStudent(getStudentByID(student2.getIndex()));
                grade2.setStudentId(student2.getIndex());

                Grade grade3 = generateGrade(getCourseByID(course3.getId()));
                grade3.setId(nextGradeId());
                grade3.setStudent(getStudentByID(student3.getIndex()));
                grade3.setStudentId(student3.getIndex());

                this.datastore.save(grade1);
                this.datastore.save(grade2);
                this.datastore.save(grade3);

                System.out.println("Init data created");
                this.printCurrentStatus();

            } catch (Exception e) {
                System.out.println("Error when creating init data");
                e.printStackTrace();
            }
        }
    }

    // next

    private synchronized Long nextStudentId() {
        Seq id = datastore.findAndModify(datastore.createQuery(Seq.class), datastore.createUpdateOperations(Seq.class).inc("studentId", 1));
        return id.getStudentId();
    }

    private synchronized Long nextCourseId() {
        Seq id = datastore.findAndModify(datastore.createQuery(Seq.class), datastore.createUpdateOperations(Seq.class).inc("courseId", 1));
        return id.getCourseId();
    }

    private synchronized Long nextGradeId() {
        Seq id = datastore.findAndModify(datastore.createQuery(Seq.class), datastore.createUpdateOperations(Seq.class).inc("gradeId", 1));
        return id.getGradeId();
    }

    // get
    public List<Student> getStudents() {
        return datastore.createQuery(Student.class).asList();
    }

    public List<Student> getStudentsFiltered(String firstNameFilter, String lastNameFilter, Date birthDate, String order) {
        Query<Student> query = datastore.createQuery(Student.class);
        if (firstNameFilter != null && !firstNameFilter.isEmpty())
            query.field("firstName").containsIgnoreCase(firstNameFilter);
        if (lastNameFilter != null && !lastNameFilter.isEmpty())
            query.field("lastName").containsIgnoreCase(lastNameFilter);
        if (birthDate != null) {
            if (order != null && order.equals("eq")) {
                query.field("dateOfBirth").equal(birthDate);
            } else if (order != null && order.equals("gt")) {
                query.field("dateOfBirth").greaterThan(birthDate);
            } else if (order != null && order.equals("lt")) {
                query.field("dateOfBirth").lessThan(birthDate);
            }
        }
        return query.asList();
    }

    public Student getStudentByID(Long id) throws NotFoundException {
        Student student = datastore.find(Student.class).field("index").equal(id).get();
        if (student == null) throw new NotFoundException();
        return student;
    }

    public List<Course> getCourses() {
        return datastore.createQuery(Course.class).asList();
    }

    public List<Course> getCoursesFiltered(String name, String lecturer) {
        Query<Course> query = datastore.createQuery(Course.class);
        if (name != null && !name.isEmpty())
            query.field("name").containsIgnoreCase(name);
        if (lecturer != null && !lecturer.isEmpty())
            query.field("lecturer").containsIgnoreCase(lecturer);
        return query.asList();
    }

    public Course getCourseByID(Long id) throws NotFoundException {
        Course course = datastore.find(Course.class).field("id").equal(id).get();
        if (course == null) throw new NotFoundException();
        return course;
    }

    public List<Grade> getGrades(Long index) throws NotFoundException {
        List<Grade> grades = datastore.createQuery(Grade.class).field("studentId").equal(index).asList();
        return grades;
    }

    public List<Grade> getGradesFiltered(Long index, int courseId, double value, String order) throws NotFoundException {
        Student student = this.getStudentByID(index);
        Query<Grade> query = datastore.find(Grade.class).field("studentId").equal(student.getIndex());
        Course course = datastore.createQuery(Course.class).field("id").equal(courseId).get();
        if (course != null) {
            query.field("course").equal(course);
        }
        if (value > 0) {
            if (order != null && order.equals("eq")) {
                query.field("grade").equal(value);
            } else if (order != null && order.equals("gt")) {
                query.field("grade").greaterThan(value);
            } else if (order != null && order.equals("lt")) {
                query.field("grade").lessThan(value);
            }
        }
        return query.asList();
    }

    public Grade getGradeByID(Student student, Long gradeId) throws NotFoundException {
        Grade grade = datastore.find(Grade.class).field("studentId").equal(student.getIndex()).field("id").equal(gradeId).get();
        if (grade == null) throw new NotFoundException();
        return grade;
    }

    // delete

    public void deleteStudent(Student student) {
        datastore.delete(datastore.find(Grade.class).field("studentId").equal(student.getIndex()));
        datastore.delete(student);
    }

    public void deleteCourse(Course course) {
        datastore.delete(datastore.find(Grade.class).field("course").equal(course));
        datastore.delete(course);
    }

    public void deleteGrade(Grade grade) {
        datastore.delete(grade);
    }

    // post

    public Student addStudent(Student newStudent) throws BadRequestException {
        if (newStudent.getFirstName() != null && newStudent.getLastName() != null && newStudent.getBirthday() != null) {
            Long index = this.nextStudentId();
            Student student = new Student(index, newStudent.getFirstName(), newStudent.getLastName(), newStudent.getBirthday());
            datastore.save(student);
            return this.getStudentByID(index);
        }
        throw new BadRequestException();
    }

    public Course addCourse(Course newCourse) throws BadRequestException {
        if (newCourse.getName() != null && newCourse.getLecturer() != null) {
            Long id = this.nextCourseId();
            Course course = new Course(id, newCourse.getName(), newCourse.getLecturer());
            datastore.save(course);
            return this.getCourseByID(id);
        }
        throw new BadRequestException();
    }

    public Grade addGrade(Student student, Course course, Grade newGrade) throws NotFoundException, BadRequestException {
        Course existedCourse = datastore.find(Course.class).field("id").equal(course.getId()).get();
        if (newGrade.getDate() != null && newGrade.getValue() > 0 &&  existedCourse != null) {
            Long id = this.nextGradeId();
            Grade grade = new Grade(id, newGrade.getValue(), newGrade.getDate(), existedCourse, student.getIndex(), student);
            datastore.save(grade);
            return this.getGradeByID(student, id);
        }

        throw new BadRequestException();
    }

    // put

    public Student updateStudent(Student student, Student newStudent) throws NotFoundException {
        if (newStudent.getBirthday() != null)
            student.setBirthday(newStudent.getBirthday());
        if (newStudent.getLastName() != null)
            student.setLastName(newStudent.getLastName());
        if (newStudent.getFirstName() != null)
            student.setFirstName(newStudent.getFirstName());

        datastore.save(student);
        return this.getStudentByID(student.getIndex());
    }

    public Course updateCourse(Course course, Course newCourse) throws NotFoundException {
        if (newCourse.getLecturer() != null)
            course.setLecturer(newCourse.getLecturer());
        if (newCourse.getName() != null)
            course.setName(newCourse.getName());

        datastore.save(course);
        return this.getCourseByID(course.getId());
    }

    public Grade updateGrade(Student student, Grade grade, Grade newGrade) throws NotFoundException {
        if (newGrade.getValue() > 0)
            grade.setValue(newGrade.getValue());
        if (newGrade.getDate() != null)
            grade.setDate(newGrade.getDate());
        if (newGrade.getCourse() != null) {
            Course course = getCourseByID(newGrade.getCourse().getId());
            grade.setCourse(course);
        }

        datastore.save(grade);
        return this.getGradeByID(student, grade.getId());
    }


}
