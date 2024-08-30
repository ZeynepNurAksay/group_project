package group07.group.allocation.model.question.set;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MultipleChoice extends Question{

    private int minimumSelection;
    private int maximumSelection;

    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ChoiceOption> options;

    public MultipleChoice (){
        this.options = new ArrayList<>();
    }

    public int getMinimumSelection() {
        return minimumSelection;
    }

    public void setMinimumSelection(int minimumSelection) {
        this.minimumSelection = minimumSelection;
    }

    public int getMaximumSelection() {
        return maximumSelection;
    }

    public void setMaximumSelection(int maximumSelection) {
        this.maximumSelection = maximumSelection;
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
