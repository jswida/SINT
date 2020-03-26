import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
public class Student {
    private int index;
    private String name;
    private String surname;
    private Date birthDate;
    private Set<Grade> grades = new HashSet<>();

    public Student() {
    }

    public Student(int index, String name, String surname, Date birthDate, Set<Grade> grades) {
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.grades = grades;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }
}
