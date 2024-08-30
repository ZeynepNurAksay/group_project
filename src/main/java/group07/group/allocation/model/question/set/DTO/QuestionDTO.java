package group07.group.allocation.model.question.set.DTO;

public class QuestionDTO {

    private String questionText;
    private String questionDescription;
    private String groupAnswersBySimilar;
    private String questionWeight;
    private String order;
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionName(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public String getGroupAnswersBySimilar() {
        return groupAnswersBySimilar;
    }

    public void setGroupAnswersBySimilar(String groupAnswersBySimilar) {
        this.groupAnswersBySimilar = groupAnswersBySimilar;
    }

    public String getQuestionWeight() {
        return questionWeight;
    }

    public void setQuestionWeight(String questionWeight) {
        this.questionWeight = questionWeight;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
