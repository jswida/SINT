package com.bartek;

import com.bartek.models.Course;
import com.bartek.models.Grade;
import com.bartek.models.Squence;
import com.bartek.models.Student;
import com.mongodb.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

import javax.ws.rs.NotFoundException;
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

    public Course getCourse(Long id) throws NotFoundException {
        Course course = (Course) datastore.find(Course.class).field("id").equal(id);
        if (course == null) throw  new NotFoundException();
        return course;
    }

    public Student getStudent(Long id) throws NotFoundException{
        Student student = (Student) datastore.find(Student.class).field("index").equal(id);
        if (student == null) throw new NotFoundException();
        return student;
    }


}
