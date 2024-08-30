package group07.group.allocation.model.question.set;

import group07.group.allocation.model.account.Convenor;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class QuestionSet {

    @Id
    @GeneratedValue
    private int id;

    private String name;
    private boolean studentChooseOwnGroup;

    @OneToMany (cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TrueFalse> trueFalseQuestions;

    @OneToMany (cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriorityList> priorityListQuestions;

    @OneToMany (cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MultipleChoice> multipleChoiceQuestions;

    @ManyToOne
    private Convenor convenor;

    private int numQuestions;

//    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL)
//    private List<PreferenceSet> preferenceSets;

    public QuestionSet() {
        this.trueFalseQuestions = new ArrayList<>();
        this.priorityListQuestions = new ArrayList<>();
        this.multipleChoiceQuestions = new ArrayList<>();
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

    public boolean isStudentChooseOwnGroup() {
        return studentChooseOwnGroup;
    }

    public void setStudentChooseOwnGroup(boolean studentChooseOwnGroup) {
        this.studentChooseOwnGroup = studentChooseOwnGroup;
    }

    public List<TrueFalse> getTrueFalseQuestions() {
        return trueFalseQuestions;
    }

    public void setTrueFalseQuestions(List<TrueFalse> trueFalseQuestions) {
        this.trueFalseQuestions = trueFalseQuestions;
    }

    public List<PriorityList> getPriorityListQuestions() {
        return priorityListQuestions;
    }

    public void setPriorityListQuestions(List<PriorityList> priorityListQuestions) {
        this.priorityListQuestions = priorityListQuestions;
    }

    public List<MultipleChoice> getMultipleChoiceQuestions() {
        return multipleChoiceQuestions;
    }

    public void setMultipleChoiceQuestions(List<MultipleChoice> multipleChoiceQuestions) {
        this.multipleChoiceQuestions = multipleChoiceQuestions;
    }

    public Convenor getConvenor() {
        return convenor;
    }

    public void setConvenor(Convenor convenor) {
        this.convenor = convenor;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }

//    public List<PreferenceSet> getPreferenceSets() {
//        return preferenceSets;
//    }
//
//    public void setPreferenceSets(List<PreferenceSet> preferenceSets) {
//        this.preferenceSets = preferenceSets;
//    }

    
}
