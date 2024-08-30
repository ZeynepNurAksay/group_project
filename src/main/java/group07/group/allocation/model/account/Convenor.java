package group07.group.allocation.model.account;

import group07.group.allocation.model.Module;
import group07.group.allocation.model.question.set.QuestionSet;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Convenor extends User {

    @OneToMany(mappedBy = "convenor")
    private List<Module> modules;

    @OneToMany(mappedBy = "convenor")
    private List<QuestionSet> questions;

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public List<QuestionSet> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionSet> questions) {
        this.questions = questions;
    }
}
