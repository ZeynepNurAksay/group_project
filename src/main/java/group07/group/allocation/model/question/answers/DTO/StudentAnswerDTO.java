package group07.group.allocation.model.question.answers.DTO;

import java.util.ArrayList;
import java.util.List;

public class StudentAnswerDTO {

    private String preferenceSetId;
    private List<TrueFalseAnswerDTO> trueFalseAnswers;
    private List<MultipleChoiceAnswerDTO> multipleChoiceAnswers;
    private List<PriorityListAnswerDTO> priorityListAnswers;
    private List<String> preferredGroupMembers;
    private String submit; //used for error messages

    public StudentAnswerDTO() {
        this.trueFalseAnswers = new ArrayList<>();
        this.multipleChoiceAnswers = new ArrayList<>();
        this.priorityListAnswers = new ArrayList<>();
        this.preferredGroupMembers = new ArrayList<>();
    }

    public StudentAnswerDTO(String id, List<TrueFalseAnswerDTO> trueFalseAnswers, List<MultipleChoiceAnswerDTO> multipleChoiceAnswers, List<PriorityListAnswerDTO> priorityListAnswers, List<String> preferredGroupMembers) {
        this.preferenceSetId = id;
        this.trueFalseAnswers = trueFalseAnswers;
        this.multipleChoiceAnswers = multipleChoiceAnswers;
        this.priorityListAnswers = priorityListAnswers;
        this.preferredGroupMembers = preferredGroupMembers;
    }

    public String getPreferenceSetId() {
        return preferenceSetId;
    }

    public void setPreferenceSetId(String preferenceSetId) {
        this.preferenceSetId = preferenceSetId;
    }

    public List<TrueFalseAnswerDTO> getTrueFalseAnswers() {
        return trueFalseAnswers;
    }

    public void setTrueFalseAnswers(List<TrueFalseAnswerDTO> trueFalseAnswers) {
        this.trueFalseAnswers = trueFalseAnswers;
    }

    public List<MultipleChoiceAnswerDTO> getMultipleChoiceAnswers() {
        return multipleChoiceAnswers;
    }

    public void setMultipleChoiceAnswers(List<MultipleChoiceAnswerDTO> multipleChoiceAnswers) {
        this.multipleChoiceAnswers = multipleChoiceAnswers;
    }

    public List<PriorityListAnswerDTO> getPriorityListAnswers() {
        return priorityListAnswers;
    }

    public void setPriorityListAnswers(List<PriorityListAnswerDTO> priorityListAnswers) {
        this.priorityListAnswers = priorityListAnswers;
    }

    public List<String> getPreferredGroupMembers() {
        return preferredGroupMembers;
    }

    public void setPreferredGroupMembers(List<String> preferredGroupMembers) {
        this.preferredGroupMembers = preferredGroupMembers;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }
}
