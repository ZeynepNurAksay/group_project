package group07.group.allocation.model.question.answers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

/**
 * The answers for a multiple choice question
 */
@Entity
public class MultipleChoiceAnswer{

    @Id
    @GeneratedValue
    private int id;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Float> score;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MultipleChoiceOptionAnswer> multipleChoiceAnswers; //what the student selected for each multiple choice option

    public MultipleChoiceAnswer() {
        this.multipleChoiceAnswers = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MultipleChoiceOptionAnswer> getMultipleChoiceAnswers() {
        return multipleChoiceAnswers;
    }

    public void setMultipleChoiceAnswers(List<MultipleChoiceOptionAnswer> multipleChoiceAnswers) {
        this.multipleChoiceAnswers = multipleChoiceAnswers;
    }

    public List<Float> getScore() {
        return score;
    }

    public void setScore(List<Float> score) {
        this.score = score;
    }
}
