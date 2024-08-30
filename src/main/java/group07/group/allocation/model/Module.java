package group07.group.allocation.model;

import group07.group.allocation.model.account.Convenor;
import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.question.answers.PreferenceSet;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Module {
    @Id
    @GeneratedValue
    private int id;
    private String code;
    private String name;

    @ManyToOne
    private Convenor convenor;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Student> students;
    private String description;

    @OneToMany(mappedBy = "module")
    private List<PreferenceSet> preferenceSets;

    public Module() {
        this.students = new ArrayList<>();
        this.preferenceSets = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Convenor getConvenor() {
        return convenor;
    }

    public void setConvenor(Convenor convenor) {
        this.convenor = convenor;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void addStudent(Student student){
        if (!containsStudent(this.students, student)){ // only add the student if they are not already in the module
            this.students.add(student);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean containsStudent(final List<Student> studentList, final Student student){
        return studentList.stream().map(Student::getEmail).anyMatch(student.getEmail()::equals);
    }

    public List<PreferenceSet> getPreferenceSets() {
        return preferenceSets;
    }

    public void setPreferenceSets(List<PreferenceSet> preferenceSets) {
        this.preferenceSets = preferenceSets;
    }

    
}

