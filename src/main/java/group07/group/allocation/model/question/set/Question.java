package group07.group.allocation.model.question.set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Question {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    public int id;
    public String questionText;
    public String questionDescription;
    public boolean groupAnswersBySimilar;
    public int questionWeight;
    public int questionOrder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public int getQuestionWeight() {
        return questionWeight;
    }

    public void setQuestionWeight(int questionWeight) {
        this.questionWeight = questionWeight;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }

    public boolean isGroupAnswersBySimilar() {
        return groupAnswersBySimilar;
    }

    public void setGroupAnswersBySimilar(boolean groupAnswersBySimilar) {
        this.groupAnswersBySimilar = groupAnswersBySimilar;
    }
}
