package group07.group.allocation.model.question.answers.DTO;


@SuppressWarnings("unused")
public class PreferenceSetDTO {
    private String name;
    private String deadline;
    private String moduleID;
    private String questionSetID;
    private String groupMinNumber;
    private String groupMaxNumber;
    private String completed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }

    public String getQuestionSetID() {
        return questionSetID;
    }

    public void setQuestionSetID(String questionSetID) {
        this.questionSetID = questionSetID;
    }

    public String getGroupMinNumber() {
        return groupMinNumber;
    }

    public void setGroupMinNumber(String groupMinNumber) {
        this.groupMinNumber = groupMinNumber;
    }

    public String getGroupMaxNumber() {
        return groupMaxNumber;
    }

    public void setGroupMaxNumber(String groupMaxNumber) {
        this.groupMaxNumber = groupMaxNumber;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }
}
