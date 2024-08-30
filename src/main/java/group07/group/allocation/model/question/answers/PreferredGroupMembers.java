package group07.group.allocation.model.question.answers;

import group07.group.allocation.model.account.Student;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PreferredGroupMembers {

    @Id
    @GeneratedValue
    private int id;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Student> students;

    public PreferredGroupMembers() {
        this.students = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
