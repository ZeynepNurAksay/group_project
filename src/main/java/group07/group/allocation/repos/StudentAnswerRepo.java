package group07.group.allocation.repos;

import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.question.answers.StudentAnswer;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface StudentAnswerRepo extends CrudRepository<StudentAnswer, Integer> {
    List<StudentAnswer> findStudentAnswerByStudent(Student student);
}
