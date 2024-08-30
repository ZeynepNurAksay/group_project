package group07.group.allocation.model.question.answers.DTO;

import group07.group.allocation.model.question.set.ChoiceOption;

public class PriorityListOptionAnswerDTO extends ChoiceOption {
    private String orderInList;

    public String getOrderInList() {
        return orderInList;
    }

    public void setOrderInList(String orderInList) {
        this.orderInList = orderInList;
    }
}
