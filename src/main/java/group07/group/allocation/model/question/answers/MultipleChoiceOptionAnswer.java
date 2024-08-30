package group07.group.allocation.model.question.answers;

import group07.group.allocation.model.question.set.ChoiceOption;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * The answer the student gave for a multiple choice option
 */
@Entity
public class MultipleChoiceOptionAnswer{

    @Id
    @GeneratedValue
    private int id;

    private String choiceName;
    private boolean selected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChoiceName() {
        return choiceName;
    }

    public void setChoiceName(String choiceName) {
        this.choiceName = choiceName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
