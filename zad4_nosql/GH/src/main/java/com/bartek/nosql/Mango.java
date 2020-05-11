package com.bartek.nosql;

import com.bartek.models.Course;
import com.bartek.models.Grade;
import com.bartek.models.Student;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import javax.ws.rs.NotFoundException;
import java.util.Date;
import java.util.List;

import static com.bartek.Storage.*;


public class Mango {
    private static final boolean debug = true;
    private static Mango instance = new Mango();
    private final Datastore datastore;

    private Mango() {
        final Morphia morphia = new Morphia();
        morphia.mapPackage("models");

        datastore = morphia.createDatastore(new MongoClient(), "sint_db_1");
        datastore.ensureIndexes();

        loadMango();
    }

    public static Mango getInstance() {
        return instance;
    }

    public void loadMango() {
        if (debug) {
            datastore.delete(datastore.createQuery(Course.class));
            datastore.delete(datastore.createQuery(Student.class));
            datastore.delete(datastore.createQuery(Grade.class));
            datastore.delete(datastore.createQuery(Seq.class));
        }
        if (datastore.getCount(Course.class) == 0 && datastore.getCount(Student.class) == 0) {
            try {
                Course course1 = generateCourse("QWER");
                Grade grade1 = generateGrade(course1);

                Course course2 = generateCourse("ASDF");
                Grade grade2 = generateGrade(course2);

                Course course3 = generateCourse("ZXCV");
                Grade grade3 = generateGrade(course3);

                Student student1 = generateStudent("Jerry", "Arry");
                Student student2 = generateStudent("Merry", "Berry");
                Student student3 = generateStudent("Terry", "Cerry");


                grade1.setStudent(student1);
                grade1.setStudentId(student1.getIndex());
                grade2.setStudent(student2);
                grade2.setStudentId(student2.getIndex());
                grade3.setStudent(student3);
                grade3.setStudentId(student3.getIndex());

                this.datastore.save(course1);
                this.datastore.save(course2);
                this.datastore.save(course3);

                this.datastore.save(grade1);
                this.datastore.save(student1);
                this.datastore.save(grade2);
                this.datastore.save(student2);
                this.datastore.save(grade3);
                this.datastore.save(student3);

            } catch (Exception ignored) {
            }
            datastore.save(new Seq(30000L, 2000L, 100L));
        }
    }

    // next

    private synchronized Long nextStudentId() {
        Seq id = datastore.findAndModify(datastore.createQuery(Seq.class), datastore.createUpdateOperations(Seq.class).inc("studentIndex", 1));
        return id.getStudentIndex();
    }

    private synchronized Long nextCourseId() {
        Seq id = datastore.findAndModify(datastore.createQuery(Seq.class), datastore.createUpdateOperations(Seq.class).inc("courseID", 1));
        return id.getCourseID();
    }

    private synchronized Long nextGradeId() {
        Seq id = datastore.findAndModify(datastore.createQuery(Seq.class), datastore.createUpdateOperations(Seq.class).inc("gradeID", 1));
        return id.getGradeID();
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
//        if (birthDate != null) {
//            if (order != null && order.equals("eq")) {
//                query.field("dateOfBirth").equal(birthDate);
//            } else if (order != null && order.equals("gt")) {
//                query.field("dateOfBirth").greaterThan(birthDate);
//            } else if (order != null && order.equals("lt")) {
//                query.field("dateOfBirth").lessThan(birthDate);
//            }
//        }
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

    public List<Course> getCoursesFiltered(String name, String supervisor) {
        Query<Course> query = datastore.createQuery(Course.class);
        if (name != null && !name.isEmpty())
            query.field("name").containsIgnoreCase(name);
        if (supervisor != null && !supervisor.isEmpty())
            query.field("lecturer").containsIgnoreCase(supervisor);
        return query.asList();
    }

    public Course getCourseByID(Long id) throws NotFoundException {
        Course course = datastore.find(Course.class).field("id").equal(id).get();
        if (course == null) throw new NotFoundException();
        return course;
    }

    public List<Grade> getGrades(Long index) throws NotFoundException {
        Student student = this.getStudentByID(index);
        if(student != null){
            return datastore.createQuery(Grade.class).field("studentIndex").equal(student.getIndex()).asList();
        }
        throw new NotFoundException();
    }

    public List<Grade> getGradesFiltered(Long index, int courseId, double value, String order) throws NotFoundException {
        Student student = this.getStudentByID(index);
        Query<Grade> query = datastore.find(Grade.class).field("studentIndex").equal(student.getIndex());
        if (courseId > 0)
            query.field("courseID").equal(courseId);
//        if (value > 0) {
//            if (order != null && order.equals("eq")) {
//                query.field("grade").equal(value);
//            } else if (order != null && order.equals("gt")) {
//                query.field("grade").hasAnyOf();
//            } else if (order != null && order.equals("lt")) {
//                query.field("grade").hasAnyOf();
//            }
//        }
        return query.asList();
    }

    public Grade getGradeByID(Student student, int gradeID) throws NotFoundException {
        Grade grade = datastore.find(Grade.class).field("studentIndex").equal(student.getIndex()).field("id").equal(gradeID).get();
        if (grade == null) throw new NotFoundException();
        return grade;
    }




}
