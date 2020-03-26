import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class Grade {

    private Long id;
    private GradeValue value;
    private Date date;
    private Subject subject;

    public Grade( ) {
    }

    public Grade(Long id, GradeValue value, Date date, Subject subject) {
        this.id = id;
        this.value = value;
        this.date = date;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GradeValue getValue() {
        return value;
    }

    public void setValue(GradeValue value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", value=" + value +
                ", date=" + date +
                ", subject=" + subject +
                '}';
    }
}
