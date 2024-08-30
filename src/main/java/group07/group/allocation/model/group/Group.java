package group07.group.allocation.model.group;

import group07.group.allocation.model.question.answers.PreferenceSet;
import group07.group.allocation.model.question.answers.StudentAnswer;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "allocated_group")
public class Group {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    private PreferenceSet preferenceSet;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<StudentAnswer> studentAnswers;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @Column(length = 3000)
    private List<String> mostCommon;

    private boolean openToNewMembers = true;
    private boolean containsNotCompletedAnswers = false;

    public Group() {
        this.studentAnswers = new ArrayList<>();
        this.mostCommon = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PreferenceSet getPreferenceSet() {
        return preferenceSet;
    }

    public void setPreferenceSet(PreferenceSet preferenceSet) {
        this.preferenceSet = preferenceSet;
    }

    public List<StudentAnswer> getStudentAnswers() {
        return studentAnswers;
    }

    public void setStudentAnswers(List<StudentAnswer> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }

    public List<String> getMostCommon() {
        return mostCommon;
    }

    public void setMostCommon(List<String> mostCommon) {
        this.mostCommon = mostCommon;
    }

    public boolean isOpenToNewMembers() {
        return openToNewMembers;
    }

    public void setOpenToNewMembers(boolean openToNewMembers) {
        this.openToNewMembers = openToNewMembers;
    }

    public boolean isContainsNotCompletedAnswers() {
        return containsNotCompletedAnswers;
    }

    public void setContainsNotCompletedAnswers(boolean containsNotCompletedAnswers) {
        this.containsNotCompletedAnswers = containsNotCompletedAnswers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id && Objects.equals(preferenceSet, group.preferenceSet) && studentAnswers.equals(group.studentAnswers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, preferenceSet, studentAnswers);
    }
}
