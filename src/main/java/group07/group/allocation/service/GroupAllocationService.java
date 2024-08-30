package group07.group.allocation.service;

import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.group.Group;
import group07.group.allocation.model.question.answers.*;
import group07.group.allocation.model.question.set.ChoiceOption;
import group07.group.allocation.repos.PreferenceSetRepo;

import java.util.*;

import group07.group.allocation.model.question.answers.PreferenceSet;
import group07.group.allocation.model.question.answers.StudentAnswer;
import group07.group.allocation.repos.StudentAnswerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupAllocationService {

    public PreferenceSetRepo preferenceSetRepo;
    public StudentAnswerRepo studentAnswerRepo;
    public PreferenceSet preferenceSet;
    public List<StudentAnswer> assignedStudents = new ArrayList<>();
    public List<StudentAnswer> notAnswered = new ArrayList<>();
    public List<StudentAnswer> studentsToReassign = new ArrayList<>();
    public List<StudentAnswer> allocatedNotAnswered = new ArrayList<>(); // Students who were used to initialise the groups as we ran out of completed answers
    public List<StudentAnswer> assignedSelf = new ArrayList<>();
    public int numberOfGroups;
    final Logger logger = LoggerFactory.getLogger(GroupAllocationService.class);

    public GroupAllocationService(PreferenceSet preferenceSet, PreferenceSetRepo preferenceSetRepo, StudentAnswerRepo studentAnswerRepo){
        this.preferenceSet = preferenceSet;
        this.studentAnswerRepo = studentAnswerRepo;
        this.preferenceSetRepo = preferenceSetRepo;
        this.allocateGroups(this.preferenceSet, this.preferenceSetRepo, this.studentAnswerRepo);
    }

    public void allocateGroups(PreferenceSet thePreferenceSet, PreferenceSetRepo thePreferenceSetRepo, StudentAnswerRepo theStudentAnswerRepo) {
        preferenceSet = thePreferenceSet;
        preferenceSetRepo = thePreferenceSetRepo;
        studentAnswerRepo = theStudentAnswerRepo;
        int preferenceSetId = preferenceSet.getId();
        long allocationTime = System.nanoTime();
        long startTime;
        logger.info(String.format("Preference Set: %s | Starting group allocation process", preferenceSetId));

        logger.info(String.format("Preference Set: %s | Started: Allocating preferred group members", preferenceSetId));
        startTime = System.nanoTime();
        getPreferredGroupMembers();
        logger.info(String.format("Preference Set: %s | Finished: Allocating preferred group members in: %sms", preferenceSetId, (System.nanoTime() - startTime) / 1000000.0));

        logger.info(String.format("Preference Set: %s | Started: Calculating question scores for all students", preferenceSetId));
        startTime = System.nanoTime();
        calculateScores();
        logger.info(String.format("Preference Set: %s | Finished: Calculating scores in: %sms", preferenceSetId, (System.nanoTime() - startTime)/ 1000000.0));

        logger.info(String.format("Preference Set: %s | Started: Creating initial groups for student assignment", preferenceSetId));
        startTime = System.nanoTime();
        initialiseGroups();
        logger.info(String.format("Preference Set: %s | Finished: Groups created in: %sms", preferenceSetId, (System.nanoTime() - startTime) / 1000000.0));

        logger.info(String.format("Preference Set: %s | Started: Populating groups", preferenceSetId));
        startTime = System.nanoTime();
        groupRemainingStudents();
        logger.info(String.format("Preference Set: %s | Finished: Populating Groups in: %sms", preferenceSetId, (System.nanoTime() - startTime) / 1000000.0));

        logger.info(String.format("Preference Set: %s | Started: Repopulating initial assignments", preferenceSetId));
        startTime = System.nanoTime();
        redoAllocations();
        preferenceSet.getStudentAnswers().addAll(allocatedNotAnswered);
        preferenceSet = preferenceSetRepo.save(preferenceSet);
        logger.info(String.format("Preference Set: %s | Finished: Repopulating completed in: %sms", preferenceSetId, (System.nanoTime() - startTime) / 1000000.0));

        logger.info(String.format("Preference Set: %s | Started: Adding not completed student answers to groups", preferenceSetId));
        startTime = System.nanoTime();
        addNotCompletedStudentsToGroup();
        logger.info(String.format("Preference Set: %s | Finished: Adding not completed student answers to groups: %sms", preferenceSetId, (System.nanoTime() - startTime) / 1000000.0));

        logger.info(String.format("Preference Set: %s | Started: Determining grouping reasons", preferenceSetId));
        startTime = System.nanoTime();
        mostChosen();
        logger.info(String.format("Preference Set: %s | Finished: Determining grouping reasons in: %sms", preferenceSetId, (System.nanoTime() - startTime) / 1000000.0));

        logger.info(String.format("Preference Set: %s | Completed group allocation process in: %sms", preferenceSetId, (System.nanoTime() - allocationTime) / 1000000.0));


        //set status of preference set and save to db
        preferenceSet.setStatus(PreferenceSetStatus.AWAITING_APPROVAL);
        preferenceSet = preferenceSetRepo.save(preferenceSet);
    }

    /**
     * Create groups from preferred group members
     */
    public void getPreferredGroupMembers(){
        //New list of existing answers
        List<StudentAnswer> theStudentAnswers = new ArrayList<>(preferenceSet.getStudentAnswers());
        OUTER:
        for (StudentAnswer studentAnswer : theStudentAnswers) {
            if (studentAnswer.getPreferredGroupMembers().size() == 0 || (assignedStudents.contains(studentAnswer))) {
                continue;
            }
            //Potential group members to check other group members preferred students.
            List<Student> currentStudentsGroupMembers = new ArrayList<>(studentAnswer.getPreferredGroupMembers());
            currentStudentsGroupMembers.add(studentAnswer.getStudent());

            //Final list of student answers that will form a group
            List<StudentAnswer> groupMembers = new ArrayList<>();

            for (Student otherStudent : studentAnswer.getPreferredGroupMembers()) {
                //Get the other students StudentAnswer to check their preferred group members
                StudentAnswer otherStudentsAnswers = preferenceSet.getStudentAnswers().stream().filter(s -> s.getStudent() == otherStudent).findFirst().orElse(null);
                if (otherStudentsAnswers == null) {
                    continue OUTER; //if the other student didn't enter any preferred group members
                }
                List<Student> otherStudentsGroupMembers = new ArrayList<>(otherStudentsAnswers.getPreferredGroupMembers());
                otherStudentsGroupMembers.add(otherStudent);
                if (!(new HashSet<>(currentStudentsGroupMembers).equals(new HashSet<>(otherStudentsGroupMembers)))) {
                    //if the two students preferred group members are not equal to each other regardless of order then look at the next studentAnswer
                    continue OUTER;
                }
                groupMembers.add(otherStudentsAnswers);
            }
            Group newGroup = new Group();
            for (StudentAnswer groupMember : groupMembers){
                groupMember.setGroup(newGroup);
            }
            studentAnswer.setGroup(newGroup);
            newGroup.setStudentAnswers(groupMembers);
            newGroup.getStudentAnswers().add(studentAnswer);
            newGroup.setMostCommon(new ArrayList<>(List.of("Students grouped themselves together")));
            newGroup.setPreferenceSet(preferenceSet);
            newGroup.setOpenToNewMembers(false);
            //Remove from the studentAnswers that are associated with the students in the new group to make processing quicker
            assignedStudents.addAll(newGroup.getStudentAnswers());
            assignedSelf.addAll(newGroup.getStudentAnswers());
            preferenceSet.getAllocatedGroups().add(newGroup);
        }
    }

    /**
     * Calculate the scores from the questions the students have answered
     */
    public void calculateScores() {
        //keeps track of which student is being used
        int theIndex = 0;
        //loop through each student answer
        for (StudentAnswer studentAnswer : preferenceSet.getStudentAnswers()) {
            //counter keeps track of which true false question so that the weight of the question can be accessed
            int counter = 0;
            //loop through each true false answer in the student answer
            for (TrueFalseAnswer answer : studentAnswer.getTrueFalseAnswers()) {
                //sets score to one if it's true, otherwise false
                int trueFalseNumber = answer.isAnswer() ? 1 : 0;
                int questionWeight = preferenceSet.getQuestionSet().getTrueFalseQuestions().get(counter).getQuestionWeight();

                answer.setScore((trueFalseNumber+1) * (questionWeight));
                counter++;
            }

            counter = 0;
            //loop through each multiple choice answer in the student answer
            for (MultipleChoiceAnswer answer : studentAnswer.getMultipleChoiceAnswers()) {
                List<Float> choiceOrder = new ArrayList<>();
                //loop through each option in the multiple choice answer
                for (MultipleChoiceOptionAnswer theChoice : answer.getMultipleChoiceAnswers()) {
                    int questionWeight = preferenceSet.getQuestionSet().getMultipleChoiceQuestions().get(counter).getQuestionWeight();
                    //sets the question score according to weight and answer given
                    if (theChoice.isSelected()) {
                        choiceOrder.add((float) (2*(questionWeight)));
                    } else {
                        choiceOrder.add((float) questionWeight);
                    }
                }
                answer.setScore(choiceOrder);
                counter++;
            }

            counter = 0;
            //loop through each priority list answer in the student answer
            for (PriorityListAnswer answer : studentAnswer.getPriorityListAnswers()) {
                List<Float> choiceOrder = new ArrayList<>();
                //loops through each option in the priority list question
                for (PriorityListOptionAnswer theChoice : answer.getPriorityListAnswers()) {
                    float aChoice = (float) theChoice.getOrderInList()/(float) (answer.getPriorityListAnswers().size()-1);
                    int questionWeight = preferenceSet.getQuestionSet().getPriorityListQuestions().get(counter).getQuestionWeight();
                    //sets score according to the weight and answer given
                    //the score for each option is stored in the order that the options were set when the question was created
                    choiceOrder.add((aChoice+1) * (questionWeight));
                }
                answer.setScore(choiceOrder);
                counter++;
            }
            preferenceSet.getStudentAnswers().set(theIndex, studentAnswer);
            theIndex++;
        }
    }

    /**
     * Initialise the groups:
     * First get the first student in the list that is not already assigned
     * Then find the least similar student from all the other students (the one with the greatest score) and put them in a new group
     * All these initial students will be re-assigned later to improve the accuracy of the groups
     */
    public void initialiseGroups() {
        //Todo: Need to check that the number of students can fit into a group
        //E.g Number of students 15, completed answers: 15, predefined: false, Min and max : 4 will specify 3 groups which is not enough for all the students
        //Todo: check that if group members specify the minimum number of people in their group repeatedly, there are still enough groups for other students
        List<StudentAnswer> assignedWithoutSelf = new ArrayList<>(assignedStudents);
        assignedWithoutSelf.removeAll(assignedSelf);
        float groupSize = (float) preferenceSet.getStudentAnswers().size() / preferenceSet.getGroupMaxNumber();
        int groupSizeInt = preferenceSet.getStudentAnswers().size() / preferenceSet.getGroupMaxNumber();
        if (groupSize == (float) groupSizeInt){
            numberOfGroups = (preferenceSet.getStudentAnswers().size() / preferenceSet.getGroupMaxNumber());
        } else {
            numberOfGroups = (preferenceSet.getStudentAnswers().size() / preferenceSet.getGroupMaxNumber()) + 1;

        }
        int groupCounter = 0;
        for (Group allocatedGroup : preferenceSet.getAllocatedGroups()){
            groupCounter += allocatedGroup.getStudentAnswers().size();
            if (groupCounter >= preferenceSet.getGroupMaxNumber()){
                numberOfGroups-= 1;
                groupCounter = 0;
            }
        }

        List<StudentAnswer> allStudents = new ArrayList<>(preferenceSet.getStudentAnswers());
        //if the student has not answered the preference set then they are not considered
        //they are added to a list which will be added to the groups after the whole process
        for (StudentAnswer studentAnswer : allStudents) {
            if (!studentAnswer.isCompleted()) {
                notAnswered.add(studentAnswer);
                preferenceSet.getStudentAnswers().remove(studentAnswer);
            }
        }
        List<StudentAnswer> allCompletedStudents = preferenceSet.getStudentAnswers();

        StudentAnswer student1 = new StudentAnswer();
        Group newGroup = new Group();

        if (allCompletedStudents.size() == 0) {
            //If we have run out of students who have completed their preferences then pull from the not completed pool
            if (notAnswered.size() == 0) {
                return; //Todo check later functions still run ok if there are no groups initialised.
            }
            student1 = notAnswered.get(0);
            newGroup.setContainsNotCompletedAnswers(true);
            notAnswered.remove(student1);
            studentsToReassign = new ArrayList<>(); //We no longer need to re-assign any students
            allocatedNotAnswered.add(student1);
        }
        else {
            //get the first student to add to a group
            for (StudentAnswer theStudent : allCompletedStudents) {
                //If the student has already been assigned due to specifying their preferred group members, continue.
                if (assignedStudents.contains(theStudent)) {
                    continue;
                }
                student1 = theStudent;
                break;
            }
            studentsToReassign.add(student1); //We want to re-assign them later, there could be a better match
        }


        newGroup.setPreferenceSet(preferenceSet);
        newGroup.setStudentAnswers(new ArrayList<>(List.of(student1)));
        student1.setGroup(newGroup);
        //remove the first student from the list of student answers in the preference set
        preferenceSet.getAllocatedGroups().add(newGroup);

        assignedStudents.add(student1);
        assignedWithoutSelf.add(student1);

        StudentAnswer newStudent;

        for (int i = 0; i < numberOfGroups - 1; i++) {
            //compares all unassigned group members to each assigned student at each iteration
            //then add up the scores of each individual student across all assigned student score comparisons
            //this gets the most different from all already assigned students
            List<StudentAnswer> allCompletedStudentsWithoutAssigned = new ArrayList<>(allCompletedStudents);
            Group otherGroup = new Group();
            allCompletedStudentsWithoutAssigned.removeAll(assignedStudents);

            if (allCompletedStudentsWithoutAssigned.size() == 0) {
                if (notAnswered.size() == 0) {
                    break;
                }
                newStudent = notAnswered.get(0);
                otherGroup.setContainsNotCompletedAnswers(true);
                notAnswered.remove(newStudent);
                allocatedNotAnswered.add(newStudent);
            }
            else {
                List<List<Float>> theScores = new ArrayList<>(Objects.requireNonNull(KNN.knnAlgorithm(preferenceSet, assignedWithoutSelf, allCompletedStudentsWithoutAssigned, assignedWithoutSelf.get(0), allCompletedStudentsWithoutAssigned.size(), false)));
                for (StudentAnswer assignedStudent : assignedWithoutSelf) {
                    //Don't compare the current student with itself
                    if (assignedStudent == assignedWithoutSelf.get(0)) {
                        continue;
                    }
                    List<List<Float>> newScores = KNN.knnAlgorithm(preferenceSet, assignedWithoutSelf, allCompletedStudentsWithoutAssigned, assignedStudent, allCompletedStudentsWithoutAssigned.size(), false);

                    int scoreIndex = 0;
                    for (List<Float> theScore : theScores) {
                        for (List<Float> newScore : Objects.requireNonNull(newScores)) {
                            if (Objects.equals(theScore.get(0), newScore.get(0))) {
                                theScores.get(scoreIndex).set(1, (theScore.get(1) + newScore.get(1)));
                            }
                        }
                        scoreIndex++;
                    }
                }
                theScores = KNN.sortListsOfListsBySecondItem(theScores);

                List<List<Float>> finalTheScores = theScores;
                newStudent = allCompletedStudents.stream().filter(s -> finalTheScores.get(finalTheScores.size()-1).get(0).intValue() == (s.getId())).findAny().orElse(null);
                allCompletedStudentsWithoutAssigned.remove(newStudent);
                studentsToReassign.add(newStudent);
            }


            otherGroup.setStudentAnswers(new ArrayList<>(List.of(Objects.requireNonNull(newStudent))));
            newStudent.setGroup(otherGroup);
            otherGroup.setPreferenceSet(preferenceSet);
            assignedStudents.add(newStudent);
            assignedWithoutSelf.add(newStudent);
            preferenceSet.getAllocatedGroups().add(otherGroup);
        }
    }

    /**
     * add all students who have completed their preferences (and not already in a group to an existing group)
     */
    public void groupRemainingStudents(){
        List<StudentAnswer> allStudentsWithoutAssigned = new ArrayList<>(preferenceSet.getStudentAnswers());
        allStudentsWithoutAssigned.removeAll(assignedStudents);

        if (allStudentsWithoutAssigned.size() == 0) {
            return; // There are no more students who have answered questions to assign
        }

        for (Group theGroup : preferenceSet.getAllocatedGroups()) {
            if (!theGroup.isOpenToNewMembers()) {
                continue;
            }
            if (allStudentsWithoutAssigned.size() == 0) {
                break;
            }
            List<List<Float>> nextSimilar = KNN.knnAlgorithm(preferenceSet, assignedStudents, allStudentsWithoutAssigned, theGroup.getStudentAnswers().get(0), 1, true);
            StudentAnswer newStudent = preferenceSet.getStudentAnswers().stream().filter(s -> (Objects.requireNonNull(nextSimilar).get(0).get(0).intValue()) == (s.getId())).findAny().orElse(null);
            theGroup.getStudentAnswers().add(newStudent);
            Objects.requireNonNull(newStudent).setGroup(theGroup);
            allStudentsWithoutAssigned.remove(newStudent);
            assignedStudents.add(newStudent);
            studentsToReassign.add(newStudent);

        }

        List<StudentAnswer> assignedNotFull = new ArrayList<>(assignedStudents);
        for (Group aGroup : preferenceSet.getAllocatedGroups()) {
            if (!aGroup.isOpenToNewMembers()) {
                assignedNotFull.removeAll(aGroup.getStudentAnswers());
            }
        }
        while (allStudentsWithoutAssigned.size() != 0) {
            StudentAnswer theStudent = assignStudentToGroup(allStudentsWithoutAssigned, assignedNotFull);

            //to find the mode, use count function and if count is greater than previous greatest count, make that the new greatest
            if (preferenceSet.getStudentAnswers().size() / 5 > studentsToReassign.size()) {
                studentsToReassign.add(theStudent);
            }
        }
    }

    //redo first 20% of allocations

    /**
     * Re-allocate the first 20% of people where allocated to a group to produce more accurate matching
     */
    public void redoAllocations() {
        List<StudentAnswer> copy_studentsToReassign = new ArrayList<>(studentsToReassign);
        for (StudentAnswer studentToReassign : copy_studentsToReassign) {
            Group oldGroup = studentToReassign.getGroup();
            if (oldGroup.getStudentAnswers().size() -1 <= 0){
                studentsToReassign.remove(studentToReassign); //Don't create a group of 0 and don't move the student
                continue;
            }
            preferenceSet.getAllocatedGroups().remove(oldGroup);
            oldGroup.getStudentAnswers().remove(studentToReassign);
            studentToReassign.setGroup(null);
            oldGroup.setOpenToNewMembers(true);
            preferenceSet.getAllocatedGroups().add(oldGroup);
        }

        List<StudentAnswer> groupedNotFull = new ArrayList<>(preferenceSet.getStudentAnswers());
        for (Group aGroup : preferenceSet.getAllocatedGroups()) {
            if (!aGroup.isOpenToNewMembers()) {
                groupedNotFull.removeAll(aGroup.getStudentAnswers());
            }
            groupedNotFull.removeAll(studentsToReassign);
        }
        while (studentsToReassign.size() != 0) {
            assignStudentToGroup(studentsToReassign, groupedNotFull);
        }
    }

    /**
     * Add any students that did not complete their preferences to random groups
     */
    public void addNotCompletedStudentsToGroup(){
        List<List<Integer>> openGroups = new ArrayList<>();
        for (Group group : preferenceSet.getAllocatedGroups()){
            if (group.isOpenToNewMembers()){
                openGroups.add(new ArrayList<>(List.of(group.getId(), group.getStudentAnswers().size())));
            }
        }
        while (notAnswered.size() != 0){
            openGroups = KNN.sortListsOfListsBySecondItemInt(openGroups);
            List<List<Integer>> finalOpenGroups = openGroups;
            Group groupToAddTo = preferenceSet.getAllocatedGroups().stream().filter(s -> finalOpenGroups.get(0).get(0).equals(s.getId())).findAny().orElse(null);
            StudentAnswer studentToAdd = notAnswered.get(0);
            studentToAdd.setGroup(groupToAddTo);
            Objects.requireNonNull(groupToAddTo).getStudentAnswers().add(studentToAdd);
            groupToAddTo.setContainsNotCompletedAnswers(true);
            assignedStudents.add(studentToAdd);
            openGroups.get(0).set(1, openGroups.get(0).get(1) + 1); //increase the number of people in the group
            if (groupToAddTo.getStudentAnswers().size() >= preferenceSet.getGroupMaxNumber()) {
                groupToAddTo.setOpenToNewMembers(false);
                openGroups.remove(0); //don't add any more students to this group
            }
            notAnswered.remove(studentToAdd);
            preferenceSet.getStudentAnswers().add(studentToAdd); //add the student back to the preference set's student answers
        }
    }

    /**
     * Go through each group and add any notes of interest to the convenor
     * E.g. if the group consists of mostly students who did not complete the preferences
     */
    public void mostChosen() {
        for (Group theGroup : preferenceSet.getAllocatedGroups()) {
            int notAnswered = 0;
            for (StudentAnswer theStudent : theGroup.getStudentAnswers()) {
                if (!theStudent.isCompleted()) {
                    notAnswered+=1;
                }
            }
            if (notAnswered>=theGroup.getStudentAnswers().size()/2) {
                theGroup.getMostCommon().add("This group contains mainly students who did not answer the preference questions.");
                continue;
            }
            List<List<Integer>> countAnswers = new ArrayList<>();
            for (StudentAnswer theStudent : theGroup.getStudentAnswers()) {
                int qCounter = 0;
                for (TrueFalseAnswer tfAnswer : theStudent.getTrueFalseAnswers()) {
                    if (countAnswers.size()<qCounter+1) {
                        countAnswers.add(new ArrayList<>());
                    }
                    if (!preferenceSet.getQuestionSet().getTrueFalseQuestions().get(qCounter).isGroupAnswersBySimilar()){
                        qCounter++;
                        continue;
                    }
                    if (countAnswers.get(qCounter).size()<1) {
                        countAnswers.get(qCounter).add(0);
                    }
                    if (tfAnswer.isAnswer()) {
                        countAnswers.get(qCounter).set(0, countAnswers.get(qCounter).get(0)+1);
                    }else {
                        countAnswers.get(qCounter).set(0, countAnswers.get(qCounter).get(0)-1);
                    }
                    qCounter++;
                }
                for (MultipleChoiceAnswer mcAnswer : theStudent.getMultipleChoiceAnswers()) {
                    if (countAnswers.size() < qCounter + 1) {
                        countAnswers.add(new ArrayList<>());
                    }
                    if (!preferenceSet.getQuestionSet().getMultipleChoiceQuestions().get(qCounter-preferenceSet.getQuestionSet().getTrueFalseQuestions().size()).isGroupAnswersBySimilar()) {
                        qCounter++;
                        continue;
                    }
                    int oCounter = 0;
                    for (MultipleChoiceOptionAnswer theAnswer : mcAnswer.getMultipleChoiceAnswers()) {
                        if (countAnswers.get(qCounter).size() < oCounter+1) {
                            countAnswers.get(qCounter).add(0);
                        }
                        if (theAnswer.isSelected()) {
                            countAnswers.get(qCounter).set(oCounter, countAnswers.get(qCounter).get(oCounter) + 1);
                        }
                        oCounter++;
                    }
                    qCounter++;
                }
                int offset = preferenceSet.getQuestionSet().getTrueFalseQuestions().size()+preferenceSet.getQuestionSet().getMultipleChoiceQuestions().size();
                for (PriorityListAnswer plAnswer : theStudent.getPriorityListAnswers()) {
                    if (countAnswers.size() < qCounter + 1) {
                        countAnswers.add(new ArrayList<>());
                    }
                    if (!preferenceSet.getQuestionSet().getPriorityListQuestions().get(qCounter-offset).isGroupAnswersBySimilar()) {
                        qCounter++;
                        continue;
                    }
                    int oCounter = 0;
                    for (PriorityListOptionAnswer ignored1 : plAnswer.getPriorityListAnswers()) {
                        if (preferenceSet.getQuestionSet().getPriorityListQuestions().get(qCounter - offset).getOptions().size() != countAnswers.get(qCounter).size()) {
                            for (ChoiceOption ignored : preferenceSet.getQuestionSet().getPriorityListQuestions().get(qCounter - offset).getOptions()) {
                                countAnswers.get(qCounter).add(0);
                            }
                        }
                        for (int i =0 ; i < preferenceSet.getQuestionSet().getPriorityListQuestions().get(qCounter-offset).getOptions().size(); i++){
                            if (i == plAnswer.getPriorityListAnswers().get(oCounter).getOrderInList()){
                                countAnswers.get(qCounter).set(i, countAnswers.get(qCounter).get(i) + oCounter);
                                break;
                            }
                        }
                        oCounter++;
                    }
                    qCounter++;
                }
            }

            int currentQuestion = 1;
            for (List<Integer> ignored : countAnswers) {
                if (preferenceSet.getQuestionSet().getTrueFalseQuestions().size()>=currentQuestion) {
                    if (!preferenceSet.getQuestionSet().getTrueFalseQuestions().get(currentQuestion-1).isGroupAnswersBySimilar()){
                        theGroup.getMostCommon().add("These students were grouped based on having different answers.");
                        currentQuestion++;
                        continue;
                    }
                    if (countAnswers.get(currentQuestion-1).get(0)>0) {
                        theGroup.getMostCommon().add("These students were grouped because they mainly selected: " + preferenceSet.getQuestionSet().getTrueFalseQuestions().get(currentQuestion-1).getOption1());
                    } else if(countAnswers.get(currentQuestion-1).get(0)<0) {
                        theGroup.getMostCommon().add("These students were grouped because they mainly selected: " + preferenceSet.getQuestionSet().getTrueFalseQuestions().get(currentQuestion-1).getOption2());
                    }else {
                        theGroup.getMostCommon().add("We were unable to group students using this question.");
                    }
                    currentQuestion++;
                }else if(preferenceSet.getQuestionSet().getTrueFalseQuestions().size()+preferenceSet.getQuestionSet().getMultipleChoiceQuestions().size()>=currentQuestion){
                    if (!preferenceSet.getQuestionSet().getMultipleChoiceQuestions().get(currentQuestion-1-preferenceSet.getQuestionSet().getTrueFalseQuestions().size()).isGroupAnswersBySimilar()){
                        theGroup.getMostCommon().add("These students were grouped based on having different answers.");
                        currentQuestion++;
                        continue;
                    }
                    int bestOption = 0;
                    int bestOptionCount = 0;
                    for (int i = 0; i < preferenceSet.getQuestionSet().getMultipleChoiceQuestions().get(currentQuestion-1-preferenceSet.getQuestionSet().getTrueFalseQuestions().size()).getOptions().size(); i++) {
                        if (countAnswers.get(currentQuestion-1).get(i)>bestOptionCount) {
                            bestOptionCount = countAnswers.get(currentQuestion-1).get(i);
                            bestOption = i;
                        }
                    }
                    theGroup.getMostCommon().add("These students were grouped because they mainly selected: " + preferenceSet.getQuestionSet().getMultipleChoiceQuestions().get(currentQuestion-1-preferenceSet.getQuestionSet().getTrueFalseQuestions().size()).getOptions().get(bestOption).getChoiceName()) ;
                    currentQuestion++;
                }else{
                    if (!preferenceSet.getQuestionSet().getPriorityListQuestions().get(currentQuestion-1-preferenceSet.getQuestionSet().getTrueFalseQuestions().size()-preferenceSet.getQuestionSet().getMultipleChoiceQuestions().size()).isGroupAnswersBySimilar()){
                        theGroup.getMostCommon().add("These students were grouped based on having different answers.");
                        currentQuestion++;
                        continue;
                    }
                    List<Integer> orderList = new ArrayList<>(countAnswers.get(currentQuestion-1));
                    StringBuilder pList = new StringBuilder("The average priority list for this question was: ");
                    List<String> theOrder = new ArrayList<>();
                    for (int i = 0; i < countAnswers.get(currentQuestion-1).size(); i++) {
                        theOrder.add(preferenceSet.getQuestionSet().getPriorityListQuestions().get(currentQuestion - 1 - preferenceSet.getQuestionSet().getTrueFalseQuestions().size() - preferenceSet.getQuestionSet().getMultipleChoiceQuestions().size()).getOptions().get(orderList.indexOf(Collections.max(orderList))).getChoiceName());
                        orderList.set(orderList.indexOf(Collections.max(orderList)), -1);
                    }
                    for (int i = theOrder.size()-1; i >= 0; i--) {
                        pList.append(theOrder.get(i));
                        pList.append(" ");
                    }
                    theGroup.getMostCommon().add(String.valueOf(pList));
                }
            }
        }
    }

    //TODO: If the number of chosen students is less than the maximum, then don't add anymore students


    /**
     * get the most similar group to the current student and add them to a group that is not full
     * @return the student that was just assigned to an existing group
     */
    public StudentAnswer assignStudentToGroup(List<StudentAnswer> allStudentsWithoutAssigned, List<StudentAnswer> assignedNotFull) {
        StudentAnswer theStudent = allStudentsWithoutAssigned.get(0);
        int theK = Math.min(assignedNotFull.size(), numberOfGroups + 1);

        List<List<Float>> mostSimilar = KNN.knnAlgorithm(preferenceSet, allStudentsWithoutAssigned, assignedNotFull, theStudent, theK, true);
        List<Group> theGroups = new ArrayList<>();
        StudentAnswer nextStudent;
        for (List<Float> studentScore : Objects.requireNonNull(mostSimilar)) {
            nextStudent = preferenceSet.getStudentAnswers().stream().filter(s -> (studentScore.get(0).intValue()) == (s.getId())).findAny().orElse(null);
            theGroups.add(Objects.requireNonNull(nextStudent).getGroup());
        }

        Group group = new Group();
        int prevMode = 0;
        for (Group countGroup : theGroups){
            if (!countGroup.isOpenToNewMembers()) {
                continue;
            }
            int currentMode = 0;
            for(Group compareGroup : theGroups){
                if (countGroup == compareGroup) {
                    currentMode ++;
                }
            }
            if (currentMode > prevMode) {
                prevMode = currentMode;
                group = countGroup;
            }
        }

        group.getStudentAnswers().add(theStudent);
        theStudent.setGroup(group);
        allStudentsWithoutAssigned.remove(theStudent);
        assignedStudents.add(theStudent);
        if (group.getStudentAnswers().size() >= preferenceSet.getGroupMaxNumber()) {
            group.setOpenToNewMembers(false);
            assignedNotFull.removeAll(group.getStudentAnswers());
        }
        return theStudent;
    }

}
