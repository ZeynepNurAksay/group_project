package group07.group.allocation.tasks;

import group07.group.allocation.model.question.answers.PreferenceSet;
import group07.group.allocation.model.question.answers.PreferenceSetStatus;
import group07.group.allocation.repos.PreferenceSetRepo;
import group07.group.allocation.repos.StudentAnswerRepo;
import group07.group.allocation.service.GroupAllocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CloseStudentSubmissionsOnDeadline implements Runnable{

    private final PreferenceSetRepo preferenceSetRepo;
    private final StudentAnswerRepo studentAnswerRepo;
    private final PreferenceSet preferenceSet;
    final Logger logger = LoggerFactory.getLogger(CloseStudentSubmissionsOnDeadline.class);

    public CloseStudentSubmissionsOnDeadline(PreferenceSet preferenceSet, PreferenceSetRepo preferenceSetRepo, StudentAnswerRepo studentAnswerRepo){
        this.preferenceSetRepo = preferenceSetRepo;
        this.studentAnswerRepo = studentAnswerRepo;
        this.preferenceSet = preferenceSet;
    }

    @Override
    public void run() {
        Optional<PreferenceSet> thePreferenceSet = preferenceSetRepo.findById(preferenceSet.getId());
        if (thePreferenceSet.isPresent()){
            PreferenceSet dbPreferenceSet = thePreferenceSet.get();
            dbPreferenceSet.setStatus(PreferenceSetStatus.ALLOCATING_GROUPS);
            dbPreferenceSet = preferenceSetRepo.save(dbPreferenceSet);
            logger.info("Closed student submissions for preference set: " + dbPreferenceSet.getId() + " " + dbPreferenceSet.getName());

            new GroupAllocationService(dbPreferenceSet, preferenceSetRepo, studentAnswerRepo);
        }
    }
}
