package group07.group.allocation.service;

import group07.group.allocation.model.question.answers.*;
import group07.group.allocation.model.question.set.MultipleChoice;
import group07.group.allocation.model.question.set.PriorityList;
import group07.group.allocation.model.question.set.TrueFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KNN {

    //knn algorithm
    //uses a list of student answers and compares it to one student answer
    //specify k for the number of student answers to return
    //specify similarity as true to return most similar student answers, otherwise specify false to get the least similar
    public static List<List<Float>> knnAlgorithm(PreferenceSet preferenceSet, List<StudentAnswer> assignedStudents, List<StudentAnswer> allStudents, StudentAnswer subjectStudent, int k, boolean similar){
        //list of scores for all students
        List<List<Float>> theScores = getScores(allStudents);
        //list of scores for subject student
        List<Float> subjectScores = new ArrayList<>();
        //list of student id and its similarity with the subject student's answers
        List<List<Float>> similarity = new ArrayList<>();
        for (List<Float> scoreList: theScores){
            //finds the subject student's scores by the id and removes it from the scores it will compare to
            if (scoreList.get(0) == subjectStudent.getId()){
                subjectScores = scoreList;
                theScores.remove(scoreList);
                break;
            }
        }
        if (subjectScores.size() == 0) {
            List<List<Float>> assignedScores = getScores(assignedStudents);
            for (List<Float> scoreList: assignedScores){
                if (scoreList.get(0) == subjectStudent.getId()){
                    subjectScores = scoreList;
                    break;
                }
            }
        }

        //performs euclidean distance for each question answer and adds it to the score
        //this euclidean distance does not square root the answer so that we can keep the data type as integer
        for (List<Float> scoreList: theScores){
            float score = 0;
            float scoreSave = 0;
            int questionNumber = 1;
            for (TrueFalse tfQuestion : preferenceSet.getQuestionSet().getTrueFalseQuestions()) {
                if (tfQuestion.isGroupAnswersBySimilar()) {
                    score += Math.sqrt(Math.pow(subjectScores.get(questionNumber) - scoreList.get(questionNumber), 2));
                }else{
                    if (scoreList.get(questionNumber) == tfQuestion.getQuestionWeight()) {
                        score += Math.sqrt(Math.pow(scoreList.get(questionNumber)*2, 2));
                    }else {
                        score += Math.sqrt(Math.pow(scoreList.get(questionNumber)/2, 2));
                    }

                }
                questionNumber++;
            }
            //if the score has changed, add 1, so it is more favoured to be different
            if (score != scoreSave){
                score+=1;
            }
            scoreSave = score;
            for (MultipleChoice mcQuestion : preferenceSet.getQuestionSet().getMultipleChoiceQuestions()) {
                for (int i = 0; i<mcQuestion.getOptions().size(); i++) {
                    if (mcQuestion.isGroupAnswersBySimilar()) {
                        score += Math.sqrt(Math.pow(subjectScores.get(questionNumber) - scoreList.get(questionNumber), 2));
                    } else {
                        if (scoreList.get(questionNumber) == mcQuestion.getQuestionWeight()) {
                            score += Math.sqrt(Math.pow(scoreList.get(questionNumber) * 2, 2));
                        } else {
                            score += Math.sqrt(Math.pow(scoreList.get(questionNumber) / 2, 2));
                        }
                    }
                    questionNumber++;
                }
            }
            if (score != scoreSave){
                score+=1;
            }
            scoreSave = score;
            for (PriorityList plQuestion : preferenceSet.getQuestionSet().getPriorityListQuestions()) {
                for (int i = 0; i < plQuestion.getOptions().size(); i++) {
                    if (plQuestion.isGroupAnswersBySimilar()) {
                        score += Math.sqrt(Math.pow(subjectScores.get(questionNumber) - scoreList.get(questionNumber), 2));
                    } else {
                        score += Math.sqrt(Math.pow(scoreList.get(questionNumber) - 3 * plQuestion.getQuestionWeight(), 2));
                    }
                    questionNumber++;
                }
            }
            if (score != scoreSave){
                score+=1;
            }
            //adds the student's id and their similarity score to the list
            List<Float> individualScore = new ArrayList<>(List.of(scoreList.get(0), score));
            similarity.add(individualScore);
        }

        //sorts list of lists, sorts on second item of each list which is the similarity score
        //first item of list is id of student that score relates to
        similarity = sortListsOfListsBySecondItem(similarity);

        //if the function wants similar items, return the first elements in the list, otherwise return the last elements in the list
        if (similarity.size()==0){
            return null;
        }
        if (similar){
            return similarity.subList(0,k);
        }else{
            return similarity.subList(similarity.size()-k, similarity.size());
        }
    }

    /**
     * Sorts the list by the second item lowest to highest
     */
    static List<List<Float>> sortListsOfListsBySecondItem(List<List<Float>> similarity) {
        similarity = similarity.stream().sorted((o1,o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = o1.get(1).compareTo(o2.get(1));
                if (c != 0) {
                    return c;
                }
            }
            return Float.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());
        return similarity;
    }

    static List<List<Integer>> sortListsOfListsBySecondItemInt(List<List<Integer>> similarity) {
        similarity = similarity.stream().sorted((o1,o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = o1.get(1).compareTo(o2.get(1));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());
        return similarity;
    }

    //get scores for knn
    public static List<List<Float>> getScores(List<StudentAnswer> studentAnswers){
        List<List<Float>> allScores = new ArrayList<>();
        //loop through each student to calculate their scores
        for (StudentAnswer answer: studentAnswers){
            //attach id to remember which student the scores belong to
            List<Float> scores = new ArrayList<>(List.of((float) answer.getId()));
            //get scores for all true false questions
            for (TrueFalseAnswer question: answer.getTrueFalseAnswers()){
                scores.add(question.getScore());
            }
            //get scores for all multiple choice questions
            for (MultipleChoiceAnswer question: answer.getMultipleChoiceAnswers()){
                scores.addAll(question.getScore());
            }
            //get scores for all priority list questions
            for (PriorityListAnswer question: answer.getPriorityListAnswers()){
                scores.addAll(question.getScore());
            }
            allScores.add(scores);
        }
        //allScores contains a list of lists. Each list contains the student id and the scores for each question/option
        return allScores;
    }

}
