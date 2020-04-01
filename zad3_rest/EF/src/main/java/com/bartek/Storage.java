package com.bartek;

import com.bartek.models.Grade;
import com.bartek.models.GradeValue;
import com.bartek.models.Student;
import com.bartek.models.Subject;

import javax.ws.rs.NotFoundException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Storage {
    private static final SecureRandom random = new SecureRandom();
    private static Set<Student> students;
    private static Set<Grade> grades;
    private static Set<Subject> subjects;
    private static Long studentID = 0L;
    private static Long gradeID = 0L;
    private static Long subjectID = 0L;

    public Storage() {
        generateSomeObjects();
    }

    public static void delete(Class<?> object, Long id){
        // student
        if (object == Student.class){
            Student student = students.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
            if (student != null) students.remove(student);
            else throw new NotFoundException();
        }
        // grade
        else if (object == Grade.class){
            Grade grade = grades.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
            if (grade != null) {
                for (Student student : students){
                    student.getGrades().remove(grade); // does it work ?
                }
                grades.remove(grade);
            }
            else throw new NotFoundException();
        }
        // subject
        else if (object == Subject.class){
            Subject subject = subjects.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
            if (subject != null) {
                for (Grade grade : grades) {
                    if (grade.getSubject().getId().equals(id)) {
                        delete(Grade.class, grade.getId());
                    }
                }
                subjects.remove(subject);
            }
            else throw new NotFoundException();
        }
    }


    public static void generateSomeObjects(){
        Subject subject1 = generateSubject("SINT");
        Grade grade1 = generateGrade(subject1);

        Subject subject2 = generateSubject("AEM");
        Grade grade2 = generateGrade(subject2);

        Subject subject3 = generateSubject("PIRO");
        Grade grade3 = generateGrade(subject3);

        Student student1 = generateStudent("John", "Doe");
        Student student2 = generateStudent("Merry", "Does");
        Student student3 = generateStudent("Charles", "Doesy");


        student1.setGrade(grade1);
        student2.setGrade(grade2);
        student3.setGrade(grade3);

        students = new HashSet<>();
        grades = new HashSet<>();
        subjects = new HashSet<>();

        students.add(student1);
        students.add(student2);
        students.add(student3);

        grades.add(grade1);
        grades.add(grade2);
        grades.add(grade3);

        subjects.add(subject1);
        subjects.add(subject2);
        subjects.add(subject3);

    }

    public static Grade generateGrade(Subject subject){
        Grade grade = new Grade();
        grade.setId(gradeID);
        grade.setValue(randomEnum(GradeValue.class));
        grade.setDate(new Date());
        grade.setSubject(subject);
        gradeID++;
        return grade;
    }

    public static Subject generateSubject(String name){
        Subject subject = new Subject();
        subject.setId(subjectID);
        subject.setName(name);
        subject.setLecturer("Jan Kowalski");
        subjectID++;
        return subject;
    }

    public static Student generateStudent(String name, String surname){
        Student student = new Student();
        student.setId(studentID);
        student.setBirthDate(new Date());
        student.setName(name);
        student.setSurname(surname);
        studentID++;
        return student;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
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

    public static Set<Subject> getSubjects() {
        return subjects;
    }

    public static void setSubjects(Set<Subject> subjects) {
        Storage.subjects = subjects;
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

    public static Long getSubjectID() {
        return subjectID;
    }

    public static void setSubjectID(Long subjectID) {
        Storage.subjectID = subjectID;
    }
}
