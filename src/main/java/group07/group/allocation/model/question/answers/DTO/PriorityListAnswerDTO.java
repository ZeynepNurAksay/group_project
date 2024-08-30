package group07.group.allocation.model.question.answers.DTO;

import group07.group.allocation.model.question.set.DTO.PriorityListDTO;

import java.util.ArrayList;
import java.util.List;

public class PriorityListAnswerDTO extends PriorityListDTO {
    private List<PriorityListOptionAnswerDTO> priorityListAnswers;

    public PriorityListAnswerDTO() {
        this.priorityListAnswers = new ArrayList<>();
    }

    public List<PriorityListOptionAnswerDTO> getPriorityListAnswers() {
        return priorityListAnswers;
    }

    public void setPriorityListAnswers(List<PriorityListOptionAnswerDTO> priorityListAnswers) {
        this.priorityListAnswers = priorityListAnswers;
    }
}
