package com.asia;

import com.asia.models.Course;
import com.asia.models.Grade;
import com.asia.models.Student;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.security.SecureRandom;
import java.util.*;

public class DataBase {
    private static final SecureRandom random = new SecureRandom();
    private static ArrayList<Student> students;
    private static ArrayList<Grade> grades;
    private static ArrayList<Course> courses;
    private static Long studentIndex = 1L;
    private static Long gradeId = 1L;
    private static Long CourseId = 1L;

    public DataBase() {
        generateSomeObjects();
    }

    public static void delete(Class<?> object, Long id) {

        if (object == Student.class) {
            Student student = students.stream().filter(s -> s.getIndex().equals(id)).findFirst().orElse(null);
            if (student != null) students.remove(student);
            else throw new NotFoundException();
        }
        else if (object == Grade.class) {
            Grade grade = grades.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
            if (grade != null) {
                for (Student student : students) {
                    student.getGrades().remove(grade);
                }
                grades.remove(grade);
            } else throw new NotFoundException();
        }
        else if (object == Course.class) {
            Course course = courses.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
            if (course != null) {
                grades.removeIf(g -> g.getCourse().getId().equals(id));
                students.forEach(s -> s.getGrades().removeIf(g -> g.getCourse().getId().equals(id)));
                courses.remove(course);
            } else throw new NotFoundException();
        }
    }

    public static Student addStudent(Student newStudent) {
        if (newStudent.getFirstName() != null && newStudent.getLastName() != null && newStudent.getBirthday() != null) {
            Student student = new Student();
            student.setIndex(studentIndex);
            student.setName(newStudent.getFirstName());
            student.setSurname(newStudent.getLastName());
            student.setBirthday(newStudent.getBirthday());
            student.setGrades(new HashSet<>());
            studentIndex++;
            students.add(student);
            return student;
        } else throw new BadRequestException();
    }

    public static Grade addGrade(Grade ng) {
        if (ng.getValue() != 0 && ng.getDate() != null && ng.getCourse().getId() != null) {
            Course course = courses.stream().filter(s -> s.getId().equals(ng.getCourse().getId())).findFirst().orElse(null);
            if (course != null) {
                Grade grade = new Grade(gradeId, ng.getValue(), ng.getDate(), ng.getCourse());
                gradeId++;
                grades.add(grade);
                return grade;
            } else throw new BadRequestException();
        } else throw new BadRequestException();
    }

    public static Grade addGradeToStudent(Grade ng, Long gradeStudentIndex) {
        Grade grade = addGrade(ng);
        Student student = students.stream().filter(s -> s.getIndex().equals(gradeStudentIndex)).findFirst().orElse(null);
        if (student != null) {
            student.getGrades().add(grade);
            return grade;
        } else throw new NotFoundException();

    }

    public static Course addCourse(Course ns) {
        if (ns.getName() != null && ns.getLecturer() != null) {
            Course course = new Course(CourseId, ns.getName(), ns.getLecturer());
            CourseId++;
            courses.add(course);
            return course;
        } else throw new BadRequestException();
    }

    public static Student updateStudent(Long updatedStudentIndex, Student newStudent){
        Student student = students.stream().filter(s -> s.getIndex().equals(updatedStudentIndex)).findFirst().orElse(null);
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
            if (newGrade.getValue() != 0) {
                grade.setValue(newGrade.getValue());
            }
            if (newGrade.getDate() != null && newGrade.getDate().toString().length() > 0) {
                grade.setDate(newGrade.getDate());
            }
            if (newGrade.getCourse().getId() != null && newGrade.getCourse().getId() >= 0){
                courses.stream().filter(s -> s.getId().equals(newGrade.getCourse().getId())).findFirst().ifPresent(grade::setCourse);
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

        Student student1 = generateStudent("Joanna", "Swida");
        Student student2 = generateStudent("Michał", "Grazikowski");
        Student student3 = generateStudent("Stanisław", "Szatniak");


        student1.setGrade(grade1);
        student2.setGrade(grade2);
        student3.setGrade(grade3);

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
        grade.setId(gradeId);
        grade.setValue(randomGradeValue());
        grade.setDate(new Date());
        grade.setCourse(course);
        grade.setStudentIndex(studentIndex);
        gradeId++;
        return grade;
    }

    public static Course generateCourse(String name) {
        Course course = new Course();
        course.setId(CourseId);
        course.setName(name);
        course.setLecturer("Mateusz Lango");
        CourseId++;
        return course;
    }

    public static Student generateStudent(String name, String surname) {
        Student student = new Student();
        student.setIndex(studentIndex);
        student.setBirthday(new Date());
        student.setName(name);
        student.setSurname(surname);
        studentIndex++;
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
        DataBase.students = students;
    }

    public static ArrayList<Grade> getGrades() {
        return grades;
    }

    public static void setGrades(ArrayList<Grade> grades) {
        DataBase.grades = grades;
    }

    public static ArrayList<Course> getCourses() {
        return courses;
    }

    public static void setCourses(ArrayList<Course> courses) {
        DataBase.courses = courses;
    }

    public static Long getStudentIndex() {
        return studentIndex;
    }

    public static void setStudentIndex(Long studentIndex) {
        DataBase.studentIndex = studentIndex;
    }

    public static Long getGradeID() {
        return gradeId;
    }

    public static void setGradeID(Long gradeID) {
        DataBase.gradeId = gradeID;
    }

    public static Long getCourseID() {
        return CourseId;
    }

    public static void setCourseID(Long CourseID) {
        DataBase.CourseId = CourseID;
    }
}
