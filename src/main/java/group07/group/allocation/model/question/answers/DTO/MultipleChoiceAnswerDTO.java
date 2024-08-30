package group07.group.allocation.model.question.answers.DTO;

import group07.group.allocation.model.question.set.DTO.MultipleChoiceDTO;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceAnswerDTO extends MultipleChoiceDTO {
    private List<MultipleChoiceOptionAnswerDTO> selectedAnswers;

    public MultipleChoiceAnswerDTO() {
        this.selectedAnswers = new ArrayList<>();
    }

    public List<MultipleChoiceOptionAnswerDTO> getSelectedAnswers() {
        return selectedAnswers;
    }

    public void setSelectedAnswers(List<MultipleChoiceOptionAnswerDTO> selectedAnswers) {
        this.selectedAnswers = selectedAnswers;
    }
}
