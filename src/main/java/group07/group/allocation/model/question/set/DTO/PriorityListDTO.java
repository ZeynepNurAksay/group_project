package group07.group.allocation.model.question.set.DTO;

import java.util.ArrayList;
import java.util.List;

public class PriorityListDTO extends QuestionDTO{
    private List<ChoiceOptionDTO> options;

    public PriorityListDTO (){
        this.options = new ArrayList<>();
    }

    public List<ChoiceOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<ChoiceOptionDTO> options) {
        this.options = options;
    }

    public void addOption(ChoiceOptionDTO option){
        this.options.add(option);
    }
}


