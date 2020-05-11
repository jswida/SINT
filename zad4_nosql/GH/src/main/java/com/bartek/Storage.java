package com.bartek;

import com.bartek.models.Course;
import com.bartek.models.Grade;
import com.bartek.models.Student;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.security.SecureRandom;
import java.util.*;

public class Storage {
    private static final SecureRandom random = new SecureRandom();
    private static ArrayList<Student> students;
    private static ArrayList<Grade> grades;
    private static ArrayList<Course> courses;
    private static Long studentID = 0L;
    private static Long gradeID = 0L;
    private static Long CourseID = 0L;

    public Storage() {
        generateSomeObjects();
    }

    public static void delete(Class<?> object, Long id) {

        // student
        if (object == Student.class) {
            Student student = students.stream().filter(s -> s.getIndex().equals(id)).findFirst().orElse(null);
            if (student != null) {
                grades.removeIf(g -> g.getStudentId() == student.getIndex());
                students.remove(student);
            }
            else throw new NotFoundException();
        }
        // grade
        else if (object == Grade.class) {
            Grade grade = grades.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
            if (grade != null) {
                grades.remove(grade);
            } else throw new NotFoundException();
        }
        // Course
        else if (object == Course.class) {
            Course course = courses.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
            if (course != null) {
                grades.removeIf(g -> g.getCourse().getId().equals(id));
                courses.remove(course);
            } else throw new NotFoundException();
        }
    }

    public static Student addStudent(Student ns) {
        if (ns.getFirstName() != null && ns.getLastName() != null && ns.getBirthday() != null) {
            Student student = new Student();
            student.setIndex(studentID);
            student.setName(ns.getFirstName());
            student.setSurname(ns.getLastName());
            student.setBirthday(ns.getBirthday());

            studentID++;
            students.add(student);
            return student;
        } else throw new BadRequestException();
    }

    public static Grade addGrade(Grade ng) {
        if (ng.getValue() != 0 && ng.getDate() != null && ng.getCourse().getId() != null) {
            Course course = courses.stream().filter(s -> s.getId().equals(ng.getCourse().getId())).findFirst().orElse(null);
            if (course != null) {
                Grade grade = new Grade(gradeID, ng.getValue(), ng.getDate(), ng.getCourse());
                gradeID++;
                grades.add(grade);
                return grade;
            } else throw new BadRequestException();
        } else throw new BadRequestException();
    }

    public static Grade addGradeToStudent(Grade ng, Long gradeStudentId) {
        Grade grade = addGrade(ng);
        Student student = students.stream().filter(s -> s.getIndex().equals(gradeStudentId)).findFirst().orElse(null);
        if (student != null) {
            grade.setStudentId(student.getIndex());
            grade.setStudent(student);
            return grade;
        } else throw new NotFoundException();

    }

    public static Course addCourse(Course ns) {
        if (ns.getName() != null && ns.getLecturer() != null) {
            Course course = new Course(CourseID, ns.getName(), ns.getLecturer());
            CourseID++;
            courses.add(course);
            return course;
        } else throw new BadRequestException();
    }

    public static Student updateStudent(Long updatedStudentId, Student newStudent){
        Student student = students.stream().filter(s -> s.getIndex().equals(updatedStudentId)).findFirst().orElse(null);
        if (student != null){

            if (newStudent.getFirstName() != null && newStudent.getFirstName().length() > 0){
                student.setName(newStudent.getFirstName());
            }

            if (newStudent.getLastName() != null && newStudent.getLastName().length() > 0){
                student.setSurname(newStudent.getLastName());
            }

            if (newStudent.getBirthday() != null && newStudent.getBirthday().toString().length() > 0){
                student.setBirthday(newStudent.getBirthday());
            }

            return student;
        }

        else throw new NotFoundException();
    }

    public static Grade updateGrade(Long updatedGradeId, Grade newGrade) {
        Grade grade = grades.stream().filter(s -> s.getId().equals(updatedGradeId)).findFirst().orElse(null);
        if (grade != null) {
            if (newGrade.getValue() != 0) {
                grade.setValue(newGrade.getValue());
            }
            if (newGrade.getDate() != null && newGrade.getDate().toString().length() > 0) {
                grade.setDate(newGrade.getDate());
            }
            if (newGrade.getCourse().getId() != null && newGrade.getCourse().getId() >= 0){
                Course course = courses.stream().filter(s -> s.getId().equals(newGrade.getCourse().getId())).findFirst().orElse(null);
                if (course != null) {
                    grade.setCourse(course);
                }
            }
            return grade;
        } else throw new NotFoundException();
    }

    public static Course updateCourse(Long updatedCourseId, Course newCourse) {
        Course course = courses.stream().filter(s -> s.getId().equals(updatedCourseId)).findFirst().orElse(null);
        if (course != null) {
            if (newCourse.getName() != null && newCourse.getName().length() > 0) {
                course.setName(newCourse.getName());
            }
            if (newCourse.getLecturer() != null && newCourse.getLecturer().length() > 0) {
                course.setLecturer(newCourse.getLecturer());
            }
            return course;
        } else throw new NotFoundException();
    }

    public static void generateSomeObjects() {
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

        students = new ArrayList<>();
        grades = new ArrayList<>();
        courses = new ArrayList<>();

        students.add(student1);
        students.add(student2);
        students.add(student3);

        grades.add(grade1);
        grades.add(grade2);
        grades.add(grade3);

        courses.add(course1);
        courses.add(course2);
        courses.add(course3);

    }

    public static Grade generateGrade(Course course) {
        Grade grade = new Grade();
        grade.setId(gradeID);
        grade.setValue(randomGradeValue());
        grade.setDate(new Date());
        grade.setCourse(course);
        gradeID++;
        return grade;
    }

    public static Course generateCourse(String name) {
        Course course = new Course();
        course.setId(CourseID);
        course.setName(name);
        course.setLecturer("Jan Kowalski");
        CourseID++;
        return course;
    }

    public static Student generateStudent(String name, String surname) {
        Student student = new Student();
        student.setIndex(studentID);
        student.setBirthday(new Date());
        student.setName(name);
        student.setSurname(surname);
        studentID++;
        return student;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static double randomGradeValue() {
        List<Double> givenList = Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0);
        Random rand = new Random();
        return givenList.get(rand.nextInt(givenList.size()));
    }

    public static ArrayList<Student> getStudents() {
        return students;
    }

    public static void setStudents(ArrayList<Student> students) {
        Storage.students = students;
    }

    public static ArrayList<Grade> getGrades() {
        return grades;
    }

    public static void setGrades(ArrayList<Grade> grades) {
        Storage.grades = grades;
    }

    public static ArrayList<Course> getCourses() {
        return courses;
    }

    public static void setCourses(ArrayList<Course> courses) {
        Storage.courses = courses;
    }

    public static Long getStudentID() {
        return studentID;
    }

    public static void setStudentID(Long studentID) {
        Storage.studentID = studentID;
    }

    public static Long getGradeID() {
        return gradeID;
    }

    public static void setGradeID(Long gradeID) {
        Storage.gradeID = gradeID;
    }

    public static Long getCourseID() {
        return CourseID;
    }

    public static void setCourseID(Long CourseID) {
        Storage.CourseID = CourseID;
    }
}
