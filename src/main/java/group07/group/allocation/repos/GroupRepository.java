package group07.group.allocation.repos;

import java.util.List;

import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.question.answers.PreferenceSetStatus;
import org.springframework.data.repository.CrudRepository;

import group07.group.allocation.model.group.Group;
import group07.group.allocation.model.question.answers.PreferenceSet;

public interface GroupRepository extends CrudRepository<Group, Integer>{
    List<Group> findGroupByPreferenceSet(PreferenceSet preferenceSet);
    List<Group> findGroupByStudentAnswersStudentAndPreferenceSetStatus(Student student, PreferenceSetStatus status);
}
