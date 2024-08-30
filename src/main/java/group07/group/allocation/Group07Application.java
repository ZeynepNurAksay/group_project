package group07.group.allocation;

import group07.group.allocation.model.Module;
import group07.group.allocation.model.account.Convenor;
import group07.group.allocation.model.account.Role;
import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.question.answers.*;
import group07.group.allocation.model.question.set.*;
import group07.group.allocation.repos.*;
import group07.group.allocation.tasks.CloseStudentSubmissionsOnDeadline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@SpringBootApplication
public class Group07Application implements ApplicationRunner {


    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    ModuleRepo moduleRepo;

    @Autowired
    QuestionSetRepo questionSetRepo;

    @Autowired
    PreferenceSetRepo preferenceSetRepo;

    @Autowired
    StudentAnswerRepo studentAnswerRepo;

    @Autowired
    Environment environment;

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    public static Role studentRole;
    public static Role convenorRole;
    public static final int NUMBER_OF_STUDENTS = 10;
    public static final int NUMBER_OF_COMPLETED_ANSWERS = 10;
    public static final boolean ADD_PRE_DEFINED_ANSWERS = false;
    public static final int MIN_NUMBER_IN_GROUP = 5;
    public static final int MAX_NUMBER_IN_GROUP = 5;

    public static void main(String[] args) {
        SpringApplication.run(Group07Application.class, args);
    }

    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void run(ApplicationArguments args) {

        Role dbStudentRole = roleRepo.findByName("STUDENT");
        Role dbConvenorRole = roleRepo.findByName("CONVENOR");

        if (dbStudentRole == null) {
            Role r1 = new Role();
            r1.setName("STUDENT");
            studentRole = roleRepo.save(r1);
        } else {
            studentRole = dbStudentRole;
        }

        if (dbConvenorRole == null) {
            Role r2 = new Role();
            r2.setName("CONVENOR");
            convenorRole = roleRepo.save(r2);
        } else {
            convenorRole = dbConvenorRole;
        }

        if (Arrays.asList(environment.getActiveProfiles()).contains("Test")) {
            return;
        }

        /*Student studentUser = new Student();
        studentUser.setEmail("student@gmail.com");
        studentUser.setFirstName("Jon");
        studentUser.setLastName("Smith");
        studentUser.setPassword(passwordEncoder().encode("123456"));
        studentUser.getUserRoles().add(studentRole);


        Student studentUser2 = new Student();
        studentUser2.setEmail("student2@gmail.com");
        studentUser2.setFirstName("Example");
        studentUser2.setLastName("2");
        studentUser2.setPassword(passwordEncoder().encode("123456"));
        studentUser2.getUserRoles().add(studentRole);

        Student studentUser3 = new Student();
        studentUser3.setEmail("student3@gmail.com");
        studentUser3.setFirstName("Example");
        studentUser3.setLastName("3");
        studentUser3.setPassword(passwordEncoder().encode("123456"));
        studentUser3.getUserRoles().add(studentRole);

        Student studentUser4 = new Student();
        studentUser4.setEmail("student4@gmail.com");
        studentUser4.setFirstName("Example");
        studentUser4.setLastName("4");
        studentUser4.setPassword(passwordEncoder().encode("123456"));
        studentUser4.getUserRoles().add(studentRole);

        Student studentUser5 = new Student();
        studentUser5.setEmail("student5@gmail.com");
        studentUser5.setFirstName("Example");
        studentUser5.setLastName("5");
        studentUser5.setPassword(passwordEncoder().encode("123456"));
        studentUser5.getUserRoles().add(studentRole);

        Student studentUser6 = new Student();
        studentUser6.setEmail("student6@gmail.com");
        studentUser6.setFirstName("Example");
        studentUser6.setLastName("6");
        studentUser6.setPassword(passwordEncoder().encode("123456"));
        studentUser6.getUserRoles().add(studentRole);

        Student studentUser7 = new Student();
        studentUser7.setEmail("student7@gmail.com");
        studentUser7.setFirstName("Example");
        studentUser7.setLastName("7");
        studentUser7.setPassword(passwordEncoder().encode("123456"));
        studentUser7.getUserRoles().add(studentRole);

        Convenor convenorUser = new Convenor();
        convenorUser.setEmail("convenor@gmail.com");
        convenorUser.setFirstName("Jess");
        convenorUser.setLastName("Berry");
        convenorUser.setPassword(passwordEncoder().encode("123456"));
        convenorUser.getUserRoles().add(convenorRole);

        Convenor convenorUser2 = new Convenor();
        convenorUser2.setEmail("convenor2@gmail.com");
        convenorUser2.setFirstName("Jess");
        convenorUser2.setLastName("Berry");
        convenorUser2.setPassword(passwordEncoder().encode("123456"));
        convenorUser2.getUserRoles().add(convenorRole);

       *//* studentUser = userRepo.save(studentUser);
        studentUser2 = userRepo.save(studentUser2);
        studentUser3 = userRepo.save(studentUser3);
        studentUser4 = userRepo.save(studentUser4);
        studentUser5 = userRepo.save(studentUser5);
        studentUser6 = userRepo.save(studentUser6);
        studentUser7 = userRepo.save(studentUser7);*//*
        convenorUser = userRepo.save(convenorUser);
        convenorUser2 = userRepo.save(convenorUser2);

        List<Student> testStudents = new ArrayList<>();
        Student studentUserTest;
//        studentUserTest.setEmail("cameronward007@gmail.com");
//        studentUserTest.setFirstName("Cameron");
//        studentUserTest.setLastName("Test");
//        studentUserTest.setPassword(passwordEncoder().encode("123456"));
//        studentUserTest.getUserRoles().add(studentRole);
//        studentUserTest = userRepo.save(studentUserTest);
//        testStudents.add(studentUserTest);
        for (int i = 1; i < NUMBER_OF_STUDENTS+1; i++) {
            studentUserTest = new Student();
            studentUserTest.setEmail("student+" + i + "@gmail.com");
            studentUserTest.setFirstName(String.valueOf(i));
            studentUserTest.setLastName("Test");
            studentUserTest.setPassword(passwordEncoder().encode("123456"));
            studentUserTest.getUserRoles().add(studentRole);
            studentUserTest = userRepo.save(studentUserTest);
            testStudents.add(studentUserTest);
        }


        Module module1 = new Module();
        module1.setCode("CO2022");
        module1.setName("Comp Sci test module 1");
        module1.setDescription("This is the description for the module. This is a test.");
        module1.setConvenor(convenorUser);
        //module1.setStudents(List.of(studentUser, studentUser2, studentUser3));

        moduleRepo.save(module1);

        Module module2 = new Module();
        module2.setCode("CO2022");
        module2.setName("Comp Sci test module 2");
        module2.setDescription("This is the description for the module. This is a test.");
        module2.setConvenor(convenorUser);
        module2.getStudents().addAll(testStudents);
        //module2.getStudents().addAll(List.of(studentUser, studentUser2, studentUser3, studentUser4, studentUser5, studentUser6, studentUser7));
        module2 = moduleRepo.save(module2);

        Module module3 = new Module();
        module3.setCode("CO4201");
        module3.setName("Comp Sci test module 3");
        module3.setDescription("This is the description for the module. This is a test.");
        module3.setConvenor(convenorUser2);
        //module3.setStudents(List.of(studentUser, studentUser2, studentUser3, studentUser4, studentUser5, studentUser6, studentUser7));

        moduleRepo.save(module3);


        List<TrueFalse> trueFalseQuestions = new ArrayList<>();
        List<PriorityList> priorityListQuestions = new ArrayList<>();
        List<MultipleChoice> multipleChoiceQuestions = new ArrayList<>();

        TrueFalse trueFalseQuestion = new TrueFalse();
        trueFalseQuestion.setQuestionText("Do you like apples");
        trueFalseQuestion.setQuestionDescription("A Yes or no question");
        trueFalseQuestion.setGroupAnswersBySimilar(true);
        trueFalseQuestion.setQuestionWeight(1);
//           trueFalseQuestion.setOrder(Integer.parseInt(trueFalseQuestionDTO.getOrder()));
        trueFalseQuestion.setQuestionOrder(0);
        trueFalseQuestion.setOption1("True");
        trueFalseQuestion.setOption2("False");
        //trueFalseQuestion = trueFalseRepo.save(trueFalseQuestion);
        trueFalseQuestions.add(trueFalseQuestion);

        TrueFalse trueFalseQuestion2 = new TrueFalse();
        trueFalseQuestion2.setQuestionText("Do you like oranges");
        trueFalseQuestion2.setQuestionDescription("A Yes or no question2");
        trueFalseQuestion2.setGroupAnswersBySimilar(false);
        trueFalseQuestion2.setQuestionWeight(1);
//           trueFalseQuestion.setOrder(Integer.parseInt(trueFalseQuestionDTO.getOrder()));
        trueFalseQuestion2.setQuestionOrder(0);
        trueFalseQuestion2.setOption1("True");
        trueFalseQuestion2.setOption2("False");
        //trueFalseQuestion2 = trueFalseRepo.save(trueFalseQuestion2);
        trueFalseQuestions.add(trueFalseQuestion2);


        PriorityList priorityListQuestion = new PriorityList();
        priorityListQuestion.setQuestionText("How do you like these cars");
        priorityListQuestion.setQuestionDescription("Rank them in order of looks");
        priorityListQuestion.setGroupAnswersBySimilar(false);
        priorityListQuestion.setQuestionWeight(1);
        priorityListQuestion.setQuestionOrder(0);
        //            trueFalseQuestion.setOrder(Integer.parseInt(trueFalseQuestionDTO.getOrder()));
        ChoiceOption car1 = new ChoiceOption();
        car1.setChoiceName("audi");
        ChoiceOption car2 = new ChoiceOption();
        car2.setChoiceName("fiat");
        ChoiceOption car3 = new ChoiceOption();
        car3.setChoiceName("seat");
        List<ChoiceOption> choices = List.of(car1, car2, car3);
        priorityListQuestion.setOptions(choices);
        priorityListQuestions.add(priorityListQuestion);

        PriorityList priorityListQuestion2 = new PriorityList();
        priorityListQuestion2.setQuestionText("How do you like these programming languages");
        priorityListQuestion2.setQuestionDescription("Rank them in order of preference highest to lowest");
        priorityListQuestion2.setGroupAnswersBySimilar(true);
        priorityListQuestion2.setQuestionWeight(100);
        priorityListQuestion2.setQuestionOrder(0);
        //            trueFalseQuestion.setOrder(Integer.parseInt(trueFalseQuestionDTO.getOrder()));
        ChoiceOption language1 = new ChoiceOption();
        language1.setChoiceName("python");
        ChoiceOption language2 = new ChoiceOption();
        language2.setChoiceName("java");
        ChoiceOption language3 = new ChoiceOption();
        language3.setChoiceName("javascript");
        ChoiceOption language4 = new ChoiceOption();
        language4.setChoiceName("sql");
        List<ChoiceOption> choices2 = List.of(language1, language2, language3, language4);
        priorityListQuestion2.setOptions(choices2);
        priorityListQuestions.add(priorityListQuestion2);


        MultipleChoice multipleChoiceQuestion = new MultipleChoice();
        multipleChoiceQuestion.setQuestionText("What do you like doing");
        multipleChoiceQuestion.setQuestionDescription("select all your favorite options");
        multipleChoiceQuestion.setGroupAnswersBySimilar(false);
        multipleChoiceQuestion.setQuestionWeight(1);
//           multipleChoiceQuestion.setOrder(Integer.parseInt(multipleChoiceDTO.getOrder()));
        multipleChoiceQuestion.setQuestionOrder(0);
        multipleChoiceQuestion.setMinimumSelection(2);
        multipleChoiceQuestion.setMaximumSelection(3);
        ChoiceOption sport1 = new ChoiceOption();
        sport1.setChoiceName("running");
        ChoiceOption sport2 = new ChoiceOption();
        sport2.setChoiceName("swimming");
        ChoiceOption sport3 = new ChoiceOption();
        sport3.setChoiceName("biking");
        ChoiceOption sport4 = new ChoiceOption();
        sport4.setChoiceName("badminton");
        List<ChoiceOption> choices3 = List.of(sport1, sport2, sport3, sport4);
        multipleChoiceQuestion.setOptions(choices3);
        multipleChoiceQuestions.add(multipleChoiceQuestion);

        MultipleChoice multipleChoiceQuestion2 = new MultipleChoice();
        multipleChoiceQuestion2.setQuestionText("What do you like doing");
        multipleChoiceQuestion2.setQuestionDescription("select your favorite option");
        multipleChoiceQuestion2.setGroupAnswersBySimilar(true);
        multipleChoiceQuestion2.setQuestionWeight(1);
//           multipleChoiceQuestion.setOrder(Integer.parseInt(multipleChoiceDTO.getOrder()));
        multipleChoiceQuestion2.setQuestionOrder(0);
        multipleChoiceQuestion2.setMinimumSelection(1);
        multipleChoiceQuestion2.setMaximumSelection(1);
        ChoiceOption activity1 = new ChoiceOption();
        activity1.setChoiceName("coding");
        ChoiceOption activity2 = new ChoiceOption();
        activity2.setChoiceName("testing");
        ChoiceOption activity3 = new ChoiceOption();
        activity3.setChoiceName("debugging");
        ChoiceOption activity4 = new ChoiceOption();
        activity4.setChoiceName("designing");
        List<ChoiceOption> choices4 = List.of(activity1, activity2, activity3, activity4);
        multipleChoiceQuestion2.setOptions(choices4);
        multipleChoiceQuestions.add(multipleChoiceQuestion2);

        QuestionSet questionSet = new QuestionSet();
        questionSet.setName("My First Question Set");
        questionSet.setStudentChooseOwnGroup(true);
        questionSet.setMultipleChoiceQuestions(multipleChoiceQuestions);
        questionSet.setPriorityListQuestions(priorityListQuestions);
        questionSet.setTrueFalseQuestions(trueFalseQuestions);
        questionSet.setConvenor(convenorUser);
        questionSet.setNumQuestions(4);
        questionSet = questionSetRepo.save(questionSet);

        MultipleChoice multipleChoiceQuestion3 = new MultipleChoice();
        multipleChoiceQuestion3.setQuestionText("What do you like doing");
        multipleChoiceQuestion3.setQuestionDescription("select your favorite option");
        multipleChoiceQuestion3.setGroupAnswersBySimilar(true);
        multipleChoiceQuestion3.setQuestionWeight(1);
//           multipleChoiceQuestion.setOrder(Integer.parseInt(multipleChoiceDTO.getOrder()));
        multipleChoiceQuestion3.setQuestionOrder(0);
        multipleChoiceQuestion3.setMinimumSelection(1);
        multipleChoiceQuestion3.setMaximumSelection(1);
        ChoiceOption activity11 = new ChoiceOption();
        activity11.setChoiceName("coding");
        ChoiceOption activity22 = new ChoiceOption();
        activity22.setChoiceName("testing");
        ChoiceOption activity33 = new ChoiceOption();
        activity33.setChoiceName("debugging");
        ChoiceOption activity44 = new ChoiceOption();
        activity44.setChoiceName("designing");
        List<ChoiceOption> choices44 = List.of(activity11, activity22, activity33, activity44);
        multipleChoiceQuestion3.setOptions(choices44);

        QuestionSet questionSet2 = new QuestionSet();
        questionSet2.setName("Second question set");
        questionSet2.setStudentChooseOwnGroup(false);
        questionSet2.setMultipleChoiceQuestions(List.of(multipleChoiceQuestion3));
        questionSet2.setConvenor(convenorUser);
        questionSet2.setNumQuestions(4);
        questionSet2 = questionSetRepo.save(questionSet2);

        PreferenceSet preferenceSet = new PreferenceSet();
        preferenceSet.setName("CW3 AI Project Preferences");
        preferenceSet.setDeadline(LocalDateTime.parse("2022-03-15T23:10"));
        preferenceSet.setGroupMaxNumber(MAX_NUMBER_IN_GROUP);
        preferenceSet.setGroupMinNumber(MIN_NUMBER_IN_GROUP);
        preferenceSet.setModule(module2);
        preferenceSet.setQuestionSet(questionSet);
        for (Student student : preferenceSet.getModule().getStudents()) {
            preferenceSet.getStudentAnswers().add(new StudentAnswer(student));
        }
        preferenceSet.setStatus(PreferenceSetStatus.AWAITING_DEADLINE);

        preferenceSet = preferenceSetRepo.save(preferenceSet);
        List<List<List<String>>> questionAnswers = new ArrayList<>();
        if (ADD_PRE_DEFINED_ANSWERS) {
            questionAnswers = new ArrayList<>(List.of(
                    List.of(
                            List.of("student+2@gmail.com", "student+3@gmail.com", "student+4@gmail.com"), List.of("True"), List.of("False"), List.of("False", "False", "True", "True"), List.of("False", "False", "True", "False"), List.of("0", "1", "2"), List.of("0", "2", "1", "3")
                    ),
                    List.of(
                            List.of("cameronward007@gmail.com", "student+3@gmail.com", "student+4@gmail.com"), List.of("True"), List.of("True"), List.of("True", "False", "True", "True"), List.of("False", "False", "False", "True"), List.of("0", "1", "2"), List.of("0", "2", "1", "3")
                    ),
                    List.of(
                            List.of("student+2@gmail.com", "cameronward007@gmail.com", "student+4@gmail.com"), List.of("True"), List.of("True"), List.of("True", "True", "False", "True"), List.of("True", "False", "False", "False"), List.of("0", "1", "2"), List.of("0", "2", "1", "3")
                    ),
                    List.of(
                            List.of("student+2@gmail.com", "student+3@gmail.com", "cameronward007@gmail.com"), List.of("False"), List.of("False"), List.of("True", "False", "True", "False"), List.of("True", "False", "False", "False"), List.of("0", "1", "2"), List.of("0", "2", "1", "3")
                    ),

                    List.of(
                            List.of("student+7@gmail.com", "student+3@gmail.com", "student+10@gmail.com"), List.of("False"), List.of("False"), List.of("True", "False", "True", "False"), List.of("False", "False", "False", "True"), List.of("2", "1", "0"), List.of("0", "3", "1", "2")
                    ),
                    List.of(
                            List.of("student+3@gmail.com", "student+3@gmail.com", "student+23@gmail.com"), List.of("False"), List.of("True"), List.of("True", "False", "False", "True"), List.of("False", "False", "True", "False"), List.of("2", "1", "0"), List.of("0", "2", "3", "1")
                    ),
                    List.of(
                            List.of("student+2@gmail.com", "student+3@gmail.com", "student+21@gmail.com"), List.of("True"), List.of("True"), List.of("False", "True", "True", "True"), List.of("False", "False", "True", "False"), List.of("2", "1", "0"), List.of("0", "2", "1", "3")
                    ),
                    List.of(
                            List.of("student+1@gmail.com", "student+3@gmail.com", "student+4@gmail.com"), List.of("True"), List.of("False"), List.of("False", "False", "True", "True"), List.of("False", "True", "False", "False"), List.of("1", "0", "2"), List.of("0", "2", "3", "1")
                    ),
                    List.of(
                            List.of(), List.of("False"), List.of("False"), List.of("True", "False", "True", "False"), List.of("False", "False", "False", "True"), List.of("2", "1", "0"), List.of("0", "3", "1", "2")
                            //List.of(), List.of("False"), List.of("False"), List.of("True", "False", "False", "True"), List.of("False", "False", "True", "False"), List.of("1", "2", "0"), List.of("3", "2", "1", "0")
                    )
            ));
        }

        int completed_answers;
        if (ADD_PRE_DEFINED_ANSWERS){
            completed_answers = NUMBER_OF_COMPLETED_ANSWERS - 9;
        } else {
            completed_answers = NUMBER_OF_COMPLETED_ANSWERS;
        }
        for (int i = 0; i < completed_answers; i++){
            List<List<String>> questionAnswer = new ArrayList<>();
            questionAnswer.add(List.of());
            String[] trueFalse = {"True", "False"};
            int tfQ1 = new Random().nextInt(trueFalse.length);
            int tfq2 = new Random().nextInt(trueFalse.length);
            questionAnswer.add(List.of(trueFalse[tfQ1]));
            questionAnswer.add(List.of(trueFalse[tfq2]));

            List<String> multipleChoice = new ArrayList<>(List.of("False", "False", "False", "False"));
            int numTrue = 0;
            for (int j = 0; j < 4; j++){
                int mcq = new Random().nextInt(trueFalse.length);
                if (mcq == 0){
                    numTrue ++;
                }
                if (numTrue == 4){
                    break;
                }
                multipleChoice.set(j, trueFalse[mcq]);
            }
            questionAnswer.add(multipleChoice);

            List<String> multipleChoice2 = new ArrayList<>(List.of("False", "False", "False", "False"));
            int truAnswer = new Random().nextInt(4);
            multipleChoice2.set(truAnswer, "True");
            questionAnswer.add(multipleChoice2);

            List<String> plq1 = new ArrayList<>();
            List<String> orders = new ArrayList<>(List.of("0", "1", "2"));
            for (int j = 0; j < 3; j++){
                int order = new Random().nextInt(orders.size());
                plq1.add(orders.get(order));
                orders.remove(order);
            }
            questionAnswer.add(plq1);

            List<String> plq2 = new ArrayList<>();
            List<String> orders2 = new ArrayList<>(List.of("0", "1", "2", "3"));
            for (int j = 0; j < 4; j++){
                int order = new Random().nextInt(orders2.size());
                plq2.add(orders2.get(order));
                orders2.remove(order);
            }
            questionAnswer.add(plq2);

            questionAnswers.add(questionAnswer);

        }

        int counter = 0;
        for (List<List<String>> questionAnswer : questionAnswers) {
            StudentAnswer studentAnswer = preferenceSet.getStudentAnswers().get(counter);
            studentAnswer.setCompleted(true);
            int questionCounter = 0;
            for (List<String> question : questionAnswer) {
                if (questionCounter == 0){
                    for (String answer: question) {
                        Student otherStudent = (Student) userRepo.findByEmail(answer);
                        studentAnswer.getPreferredGroupMembers().add(otherStudent);
                    }
                }
                else if (questionCounter == 1 || questionCounter == 2){
                    TrueFalseAnswer trueFalseAnswer = new TrueFalseAnswer();
                    trueFalseAnswer.setAnswer(Boolean.parseBoolean(question.get(0)));
                    studentAnswer.getTrueFalseAnswers().add(trueFalseAnswer);
                }
                else if (questionCounter == 3 || questionCounter == 4){
                    MultipleChoiceAnswer multipleChoiceAnswer = new MultipleChoiceAnswer();
                    for (String answer : question){
                        MultipleChoiceOptionAnswer optionAnswer = new MultipleChoiceOptionAnswer();
                        optionAnswer.setSelected(Boolean.parseBoolean(answer));
                        multipleChoiceAnswer.getMultipleChoiceAnswers().add(optionAnswer);
                    }
                    studentAnswer.getMultipleChoiceAnswers().add(multipleChoiceAnswer);
                }
                else if (questionCounter == 5 || questionCounter == 6){
                    PriorityListAnswer priorityListAnswer = new PriorityListAnswer();
                    for (String answer : question){
                        PriorityListOptionAnswer optionAnswer = new PriorityListOptionAnswer();
                        optionAnswer.setOrderInList(Integer.parseInt(answer));
                        priorityListAnswer.getPriorityListAnswers().add(optionAnswer);
                    }
                    studentAnswer.getPriorityListAnswers().add(priorityListAnswer);
                }
                questionCounter ++;
            }
            preferenceSet.getStudentAnswers().set(counter, studentAnswer);
            preferenceSet = preferenceSetRepo.save(preferenceSet);
            counter++;
        }


        //preferenceSet = preferenceSetRepo.save(preferenceSet);

        taskScheduler.schedule(new CloseStudentSubmissionsOnDeadline(preferenceSet, preferenceSetRepo, studentAnswerRepo), java.sql.Timestamp.valueOf(preferenceSet.getDeadline()));

        PreferenceSet preferenceSet2 = new PreferenceSet();
        preferenceSet2.setName("CW4 UI Project Preferences");
        preferenceSet2.setDeadline(LocalDateTime.parse("2022-04-11T17:49"));
        preferenceSet2.setGroupMaxNumber(4);
        preferenceSet2.setGroupMinNumber(3);
        preferenceSet2.setModule(module2);
        preferenceSet2.setQuestionSet(questionSet2);
        for (Student student : preferenceSet2.getModule().getStudents()) {
            preferenceSet2.getStudentAnswers().add(new StudentAnswer(student));
        }
        preferenceSet2.setStatus(PreferenceSetStatus.AWAITING_DEADLINE);
        //preferenceSetRepo.save(preferenceSet2);*/
    }
}