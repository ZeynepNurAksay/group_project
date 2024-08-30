package group07.group.allocation.model.question.set;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PriorityList extends Question{

    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ChoiceOption> options;

    public PriorityList () {
        this.options = new ArrayList<>();
    }

    public List<ChoiceOption> getOptions() {
        return options;
    }

    public void setOptions(List<ChoiceOption> options) {
        this.options = options;
    }

    public void addOption(ChoiceOption option){
        this.options.add(option);
    }

}
