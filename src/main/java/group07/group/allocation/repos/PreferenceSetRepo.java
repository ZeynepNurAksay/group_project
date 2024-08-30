package group07.group.allocation.repos;

import group07.group.allocation.model.Module;
import group07.group.allocation.model.account.Convenor;
import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.question.answers.PreferenceSet;
import group07.group.allocation.model.question.answers.PreferenceSetStatus;
import group07.group.allocation.model.question.answers.StudentAnswer;
import group07.group.allocation.model.question.set.QuestionSet;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PreferenceSetRepo extends CrudRepository<PreferenceSet, Integer> {
    List<PreferenceSet> findPreferenceSetByModule(Module module);
    List<PreferenceSet> findPreferenceSetByModuleConvenor(Convenor convenor);
    List<PreferenceSet> findPreferenceSetByModuleStudentsContainingAndStatus(Student student, PreferenceSetStatus status);
    List<PreferenceSet> findPreferenceSetByQuestionSetIdAndStatus(int id, PreferenceSetStatus status);
    PreferenceSet findPreferenceSetByIdAndModuleStudentsContainingAndStatus(int id, Student student, PreferenceSetStatus status);
}
