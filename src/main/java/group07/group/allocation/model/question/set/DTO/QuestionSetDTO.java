package group07.group.allocation.model.question.set.DTO;

import java.util.ArrayList;
import java.util.List;

public class QuestionSetDTO {
    private String name;
    private String studentChooseOwnGroup;
    private List<TrueFalseDTO> trueFalse;
    private List<PriorityListDTO> priorityList;
    private List<MultipleChoiceDTO> multipleChoice;

    public QuestionSetDTO() {
        this.trueFalse = new ArrayList<>();
        this.priorityList = new ArrayList<>();
        this.multipleChoice = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentChooseOwnGroup() {
        return studentChooseOwnGroup;
    }

    public void setStudentChooseOwnGroup(String studentChooseOwnGroup) {
        this.studentChooseOwnGroup = studentChooseOwnGroup;
    }

    public List<TrueFalseDTO> getTrueFalse() {
        return trueFalse;
    }

    public void setTrueFalse(List<TrueFalseDTO> trueFalse) {
        this.trueFalse = trueFalse;
    }

    public List<MultipleChoiceDTO> getMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(List<MultipleChoiceDTO> multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public List<PriorityListDTO> getPriorityList() {
        return priorityList;
    }

    public void setPriorityList(List<PriorityListDTO> priorityList) {
        this.priorityList = priorityList;
    }
}
