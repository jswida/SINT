import java.security.SecureRandom;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        System.out.println("start");

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
        student1.setGrade(grade2);
        student2.setGrade(grade2);
        student2.setGrade(grade3);
        student3.setGrade(grade3);

        System.out.println(student1.toString());
        System.out.println(student2.toString());
        System.out.println(student3.toString());

    }

    public static Grade generateGrade(Subject subject){
        Grade grade = new Grade();

        grade.setValue(randomEnum(GradeValue.class));
        grade.setDate(new Date());
        grade.setSubject(subject);

        return grade;
    }

    public static Subject generateSubject(String name){
        Subject subject = new Subject();
        subject.setName(name);
        subject.setLecturer("Jan Kowalski");
        return subject;
    }

    public static Student generateStudent(String name, String surname){
        Student student = new Student();
        int randomNum = ThreadLocalRandom.current().nextInt(111111, 999999 + 1);
        student.setIndex(randomNum);
        student.setBirthDate(new Date());
        student.setName(name);
        student.setSurname(surname);
        return student;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

}
