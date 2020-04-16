package com.bartek;

import com.bartek.models.Grade;
import com.bartek.models.GradeValue;
import com.bartek.models.Student;
import com.bartek.models.Course;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Storage {
    private static final SecureRandom random = new SecureRandom();
    private static Set<Student> students;
    private static Set<Grade> grades;
    private static Set<Course> courses;
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
            if (student != null) students.remove(student);
            else throw new NotFoundException();
        }
        // grade
        else if (object == Grade.class) {
            Grade grade = grades.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
            if (grade != null) {
                for (Student student : students) {
                    student.getGrades().remove(grade); // does it work ?
                }
                grades.remove(grade);
            } else throw new NotFoundException();
        }
        // Course
        else if (object == Course.class) {
            Course course = courses.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
            if (course != null) {
                for (Grade grade : grades) {
                    if (grade.getCourse().getId().equals(id)) {
                        delete(Grade.class, grade.getId());
                    }
                }
                courses.remove(course);
            } else throw new NotFoundException();
        }
    }

    public static Student addStudent(Student ns) {
        if (ns.getFirstName() != null && ns.getLastName() != null && ns.getBrithday() != null) {
            Student student = new Student();
            student.setIndex(studentID);
            student.setName(ns.getFirstName());
            student.setSurname(ns.getLastName());
            student.setBirthDate(ns.getBrithday());
//            if (ns.getGrades() != null){
//                student.setGrades(ns.getGrades());
//            }
//            else {
            student.setGrades(new HashSet<>());

            studentID++;
            students.add(student);
            return student;
        } else throw new BadRequestException();
    }

    public static Grade addGrade(Grade ng) {
        if (ng.getValue() != null && ng.getDate() != null && ng.getCourse().getId() != null) {
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
            student.getGrades().add(grade);
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
        System.out.println("print: " + + updatedStudentId + newStudent.toString());
        if (student != null){

            if (newStudent.getFirstName() != null && newStudent.getFirstName().length() > 0){
                student.setName(newStudent.getFirstName());
            }

            if (newStudent.getLastName() != null && newStudent.getLastName().length() > 0){
                student.setSurname(newStudent.getLastName());
            }

            if (newStudent.getBrithday() != null && newStudent.getBrithday().toString().length() > 0){
                student.setBirthDate(newStudent.getBrithday());
            }

            if (newStudent.getGrades() != null ){
                Set<Grade> newGrades = newStudent.getGrades();
                for (Grade grade : newGrades){
                    if (grade != null) {
                        student.getGrades().add(grade);
                    }
                }
            }
            return student;
        }

        else throw new NotFoundException();
    }

    public static Grade updateGrade(Long updatedGradeId, Grade newGrade) {
        Grade grade = grades.stream().filter(s -> s.getId().equals(updatedGradeId)).findFirst().orElse(null);
        if (grade != null) {
            if (newGrade.getValue() != null && newGrade.getValue().toString().length() > 0) {
                grade.getValue().setValue(newGrade.getValue().getValue());
//                grade.setValue(newGrade.getValue());
            }
            if (newGrade.getDate() != null && newGrade.getDate().toString().length() > 0) {
                grade.setDate(newGrade.getDate());
            }
            if (newGrade.getCourse().getId() != null && newGrade.getCourse().getId() >= 0){
                Course course = courses.stream().filter(s -> s.getId().equals(newGrade.getCourse().getId())).findFirst().orElse(null);
                if (course != null) {
                    grade.setCourse(newGrade.getCourse());
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
        Course course1 = generateCourse("SINT");
        Grade grade1 = generateGrade(course1);

        Course course2 = generateCourse("AEM");
        Grade grade2 = generateGrade(course2);

        Course course3 = generateCourse("PIRO");
        Grade grade3 = generateGrade(course3);

        Student student1 = generateStudent("John", "Doe");
        Student student2 = generateStudent("Merry", "Does");
        Student student3 = generateStudent("Charles", "Doesy");


        student1.setGrade(grade1);
        student2.setGrade(grade2);
        student3.setGrade(grade3);

        students = new HashSet<>();
        grades = new HashSet<>();
        courses = new HashSet<>();

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
        grade.setValue(randomEnum(GradeValue.class));
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
        student.setBirthDate(new Date());
        student.setName(name);
        student.setSurname(surname);
        studentID++;
        return student;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static Set<Student> getStudents() {
        return students;
    }

    public static void setStudents(Set<Student> students) {
        Storage.students = students;
    }

    public static Set<Grade> getGrades() {
        return grades;
    }

    public static void setGrades(Set<Grade> grades) {
        Storage.grades = grades;
    }

    public static Set<Course> getCourses() {
        return courses;
    }

    public static void setCourses(Set<Course> courses) {
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
