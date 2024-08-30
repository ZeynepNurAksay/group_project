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
public class PriorityListAnswer{

    @Id
    @GeneratedValue
    private int id;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Float> score;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriorityListOptionAnswer> priorityListAnswers; //what the student selected for each priority list option

    public PriorityListAnswer() {
        this.priorityListAnswers = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<PriorityListOptionAnswer> getPriorityListAnswers() {
        return priorityListAnswers;
    }

    public void setPriorityListAnswers(List<PriorityListOptionAnswer> priorityListAnswers) {
        this.priorityListAnswers = priorityListAnswers;
    }

    public List<Float> getScore() {
        return score;
    }

    public void setScore(List<Float> score) {
        this.score = score;
    }
}
