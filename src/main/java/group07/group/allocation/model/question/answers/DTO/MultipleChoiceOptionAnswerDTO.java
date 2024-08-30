package group07.group.allocation.model.question.answers.DTO;

import group07.group.allocation.model.question.set.ChoiceOption;

public class MultipleChoiceOptionAnswerDTO extends ChoiceOption {
    private String selected;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
