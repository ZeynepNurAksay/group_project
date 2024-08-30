package group07.group.allocation.model.question.answers;

import group07.group.allocation.model.group.Group;
import group07.group.allocation.model.question.set.QuestionSet;
import group07.group.allocation.model.Module;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class PreferenceSet {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private LocalDateTime deadline;

    @ManyToOne
    private Module module;

    @ManyToOne
    private QuestionSet questionSet;

    private int groupMinNumber;
    private int groupMaxNumber;
    private PreferenceSetStatus status;

    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<StudentAnswer> studentAnswers;

    private boolean completed;

    @OneToMany (cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Group> allocatedGroups;



    public PreferenceSet() {
        studentAnswers = new ArrayList<>();
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    public Module getModule() {
        return module;
    }
    public void setModule(Module module) {
        this.module = module;
    }
    public int getGroupMinNumber() {
        return groupMinNumber;
    }
    public void setGroupMinNumber(int min_number) {
        this.groupMinNumber = min_number;
    }
    public int getGroupMaxNumber() {
        return groupMaxNumber;
    }
    public void setGroupMaxNumber(int max_Number) {
        this.groupMaxNumber = max_Number;
    }
    public QuestionSet getQuestionSet() {
        return questionSet;
    }
    public void setQuestionSet(QuestionSet questionSet) {
        this.questionSet = questionSet;
    }

    public PreferenceSetStatus getStatus() {
        return status;
    }

    public void setStatus(PreferenceSetStatus status) {
        this.status = status;
    }

    public List<StudentAnswer> getStudentAnswers() {
        return studentAnswers;
    }

    public void setStudentAnswers(List<StudentAnswer> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<Group> getAllocatedGroups() {
        return allocatedGroups;
    }

    public void setAllocatedGroups(List<Group> allocatedGroups) {
        this.allocatedGroups = allocatedGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreferenceSet that = (PreferenceSet) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
