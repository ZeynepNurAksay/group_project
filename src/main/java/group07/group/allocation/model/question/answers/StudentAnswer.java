package group07.group.allocation.model.question.answers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.group.Group;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class StudentAnswer {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;
    //List of TrueFalseAnswers
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TrueFalseAnswer> trueFalseAnswers;

    //List of MultipleChoiceAnswers
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MultipleChoiceAnswer> multipleChoiceAnswers;

    //List of PriorityListAnswers
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriorityListAnswer> priorityListAnswers;

    //List of PreferredGroupMembers
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Student> preferredGroupMembers;

    @OneToOne (cascade = CascadeType.ALL)
    private Group group;

    @ManyToOne
    private Student student;

    private boolean completed;

    public StudentAnswer() {
        trueFalseAnswers = new ArrayList<>();
        multipleChoiceAnswers = new ArrayList<>();
        priorityListAnswers = new ArrayList<>();
        preferredGroupMembers = new ArrayList<>();
        this.completed = false;
    }

    public StudentAnswer(Student student) {
        trueFalseAnswers = new ArrayList<>();
        multipleChoiceAnswers = new ArrayList<>();
        priorityListAnswers = new ArrayList<>();
        preferredGroupMembers = new ArrayList<>();
        this.student = student;
        this.completed = false;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TrueFalseAnswer> getTrueFalseAnswers() {
        return trueFalseAnswers;
    }

    public void setTrueFalseAnswers(List<TrueFalseAnswer> trueFalseAnswers) {
        this.trueFalseAnswers = trueFalseAnswers;
    }

    public List<MultipleChoiceAnswer> getMultipleChoiceAnswers() {
        return multipleChoiceAnswers;
    }

    public void setMultipleChoiceAnswers(List<MultipleChoiceAnswer> multipleChoiceAnswers) {
        this.multipleChoiceAnswers = multipleChoiceAnswers;
    }

    public List<Student> getPreferredGroupMembers() {
        return preferredGroupMembers;
    }

    public void setPreferredGroupMembers(List<Student> preferredGroupMembers) {
        this.preferredGroupMembers = preferredGroupMembers;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<PriorityListAnswer> getPriorityListAnswers() {
        return priorityListAnswers;
    }

    public void setPriorityListAnswers(List<PriorityListAnswer> priorityListAnswers) {
        this.priorityListAnswers = priorityListAnswers;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentAnswer that = (StudentAnswer) o;
        return id == that.id && student.equals(that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student);
    }
}