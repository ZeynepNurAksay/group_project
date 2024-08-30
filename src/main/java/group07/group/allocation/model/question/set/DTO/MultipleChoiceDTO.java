package group07.group.allocation.model.question.set.DTO;

import group07.group.allocation.model.question.set.ChoiceOption;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceDTO extends QuestionDTO{
    private String minimumSelection;
    private String maximumSelection;
    private List<ChoiceOption> options;

    public MultipleChoiceDTO () {
        this.options = new ArrayList<>();
    }

    public String getMinimumSelection() {
        return minimumSelection;
    }

    public void setMinimumSelection(String minimumSelection) {
        this.minimumSelection = minimumSelection;
    }

    public String getMaximumSelection() {
        return maximumSelection;
    }

    public void setMaximumSelection(String maximumSelection) {
        this.maximumSelection = maximumSelection;
    }

    public List<ChoiceOption> getOptions() {
        return options;
    }

    public void setOptions(List<ChoiceOption> options) {
        this.options = options;
    }
}
