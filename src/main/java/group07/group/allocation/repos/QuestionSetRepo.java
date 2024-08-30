package group07.group.allocation.repos;

import group07.group.allocation.model.account.Convenor;
import group07.group.allocation.model.question.set.QuestionSet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuestionSetRepo extends CrudRepository<QuestionSet, Integer> {
    List<QuestionSet> findQuestionSetByConvenor(Convenor convenor);
}
