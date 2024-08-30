package group07.group.allocation.controller.convenor;

import group07.group.allocation.controller.MainController;
import group07.group.allocation.model.Module;
import group07.group.allocation.model.account.Convenor;
import group07.group.allocation.model.account.User;
import group07.group.allocation.model.question.answers.DTO.StudentAnswerDTO;
import group07.group.allocation.model.question.answers.PreferenceSet;
import group07.group.allocation.model.question.answers.PreferenceSetStatus;
import group07.group.allocation.model.question.set.*;
import group07.group.allocation.model.question.set.DTO.*;
import group07.group.allocation.repos.ModuleRepo;
import group07.group.allocation.repos.PreferenceSetRepo;
import group07.group.allocation.repos.QuestionSetRepo;
import group07.group.allocation.repos.UserRepo;
import group07.group.allocation.validator.QuestionSetValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static group07.group.allocation.controller.MainController.setErrorMessageSession;
import static group07.group.allocation.controller.convenor.ConvenorController.*;

/**
 * Allows the convenor to add / remove questionSets
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("/convenor/")  //all convenor paths are controlled here
public class ConvenorQuestionSetController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ModuleRepo moduleRepo;

    @Autowired
    QuestionSetRepo questionSetRepo;

    @Autowired
    PreferenceSetRepo preferenceSetRepo;

    @InitBinder("questionSetDTO")
    protected void initQuestionSetBinder(WebDataBinder binder) {
        binder.addValidators(new QuestionSetValidator());
    }

    /**
     * Display the manage question dashboard
     **/
    @GetMapping("/manage-questions")
    public String manageQuestionDashboard() {
        return "convenor/manage-questions";
    }

    /**
     * Create new question set page
     */
    @GetMapping("/question-set")
    public String createQuestionSetPage(@ModelAttribute("questionSetDTO") QuestionSetDTO questionSetDTO, Model model) {
        model.addAttribute("questionSetDTO", new QuestionSetDTO());
        model.addAttribute("trueFalseNum", 0);
        model.addAttribute("priorityListNum", 0);
        model.addAttribute("multiChoiceNum", 0);
        model.addAttribute("questionSetID", "");
        return "convenor/CreateQuestionSet";
    }

    /**
     * Remove a specified question set
     *
     * @param id question set id to remove
     */
    @GetMapping("/delete-question-set/{id}")
    public String removeQuestionSet(@PathVariable int id, Principal principal, HttpServletRequest request) {
        List<QuestionSet> questionSets =  getConvenorsQuestionSets(principal.getName(), userRepo, questionSetRepo);
        for (QuestionSet questionSet : questionSets) {
            if (questionSet.getId() == id) {
                questionSetRepo.deleteById(id);
                request.getSession().setAttribute("message", "Success, you have deleted a question set.");
                return "redirect:/convenor/question-sets/";
            }
        }
        request.getSession().setAttribute("Error", "The question set does not exist or you do not have permission to remove this question set");
        return "redirect:/convenor/question-sets/";
    }

    /**
     * Display a list of the module convenors question set
     */
    @GetMapping("/question-sets")
    public String showQuestionSets(Model model, Principal principal, HttpSession session) {
        if (session.getAttribute("message") != null) {
            model.addAttribute("bannerColor", "banner-color-green");
            model.addAttribute("message", session.getAttribute("message"));
            model.addAttribute("hidden", "show");
            session.removeAttribute("message");
        }
        MainController.getErrorMessageSession(model, session);
        model.addAttribute("questionSets", getConvenorsQuestionSets(principal.getName(), userRepo, questionSetRepo));
        return "convenor/questionSets";
    }


    /**
     * Edit an existing question-set based on the question set ID
     */
    @GetMapping("/question-set/{id}")
    public String editQuestionSet(@ModelAttribute("questionSetDTO") QuestionSetDTO questionSetDTO, @PathVariable int id, Principal principal,
                                  HttpServletRequest request,
                                  Model model) {
        List<QuestionSet> questionSets = getConvenorsQuestionSets(principal.getName(), userRepo, questionSetRepo);
        for (QuestionSet questionSet : questionSets) {
            if (questionSet.getId() == id) {
                List<PreferenceSet> preferenceSets = preferenceSetRepo.findPreferenceSetByQuestionSetIdAndStatus(questionSet.getId(), PreferenceSetStatus.AWAITING_DEADLINE);
                if (preferenceSets.size() > 0){
                    setErrorMessageSession(model, request, "You cannot edit this question set while students are still answering questions in a preference set. Wait until the deadline has passed or delete the preference set");
                    return "redirect:/convenor/question-sets";
                }
                List<TrueFalseDTO> trueFalseQuestions = new ArrayList<>();
                List<PriorityListDTO> priorityListQuestions = new ArrayList<>();
                List<MultipleChoiceDTO> multipleChoiceQuestions = new ArrayList<>();

                for (TrueFalse trueFalseQuestion : questionSet.getTrueFalseQuestions()) {
                    TrueFalseDTO trueFalseQuestionDTO = new TrueFalseDTO();
                    trueFalseQuestionDTO.setQuestionText(trueFalseQuestion.getQuestionText());
                    trueFalseQuestionDTO.setQuestionDescription(trueFalseQuestion.getQuestionDescription());
                    trueFalseQuestionDTO.setGroupAnswersBySimilar(String.valueOf(trueFalseQuestion.isGroupAnswersBySimilar()));
                    trueFalseQuestionDTO.setQuestionWeight(String.valueOf(trueFalseQuestion.getQuestionWeight()));
                    trueFalseQuestionDTO.setOrder(String.valueOf(0));
                    trueFalseQuestionDTO.setOption1(trueFalseQuestion.getOption1());
                    trueFalseQuestionDTO.setOption2(trueFalseQuestion.getOption2());
                    trueFalseQuestions.add(trueFalseQuestionDTO);
                }

                for (PriorityList priorityList : questionSet.getPriorityListQuestions()) {
                    PriorityListDTO priorityListQuestionDTO = new PriorityListDTO();
                    priorityListQuestionDTO.setQuestionText(priorityList.getQuestionText());
                    priorityListQuestionDTO.setQuestionDescription(priorityList.getQuestionDescription());
                    priorityListQuestionDTO.setGroupAnswersBySimilar(String.valueOf(priorityList.isGroupAnswersBySimilar()));
                    priorityListQuestionDTO.setQuestionWeight(String.valueOf(priorityList.getQuestionWeight()));
                    priorityListQuestionDTO.setOrder(String.valueOf(0));
                    for (ChoiceOption choices : priorityList.getOptions()) {
                        ChoiceOptionDTO option = new ChoiceOptionDTO();
                        option.setChoiceName(choices.getChoiceName());
                        priorityListQuestionDTO.addOption(option);
                    }
                    priorityListQuestions.add(priorityListQuestionDTO);
                }

                for (MultipleChoice multipleChoice : questionSet.getMultipleChoiceQuestions()) {
                    MultipleChoiceDTO multipleChoiceQuestionDTO = new MultipleChoiceDTO();
                    multipleChoiceQuestionDTO.setQuestionText(multipleChoice.getQuestionText());
                    multipleChoiceQuestionDTO.setQuestionDescription(multipleChoice.getQuestionDescription());
                    multipleChoiceQuestionDTO.setGroupAnswersBySimilar(String.valueOf(multipleChoice.isGroupAnswersBySimilar()));
                    multipleChoiceQuestionDTO.setQuestionWeight(String.valueOf(multipleChoice.getQuestionWeight()));
                    multipleChoiceQuestionDTO.setOrder(String.valueOf(0));
                    multipleChoiceQuestionDTO.setMinimumSelection(String.valueOf(multipleChoice.getMinimumSelection()));
                    multipleChoiceQuestionDTO.setMaximumSelection(String.valueOf(multipleChoice.getMaximumSelection()));
                    List<ChoiceOption> choices = multipleChoice.getOptions();
                    multipleChoiceQuestionDTO.setOptions(choices);
                    multipleChoiceQuestions.add(multipleChoiceQuestionDTO);
                }

                questionSetDTO.setName(questionSet.getName());
                questionSetDTO.setStudentChooseOwnGroup(String.valueOf(questionSet.isStudentChooseOwnGroup()));
                questionSetDTO.setMultipleChoice(multipleChoiceQuestions);
                questionSetDTO.setPriorityList(priorityListQuestions);
                questionSetDTO.setTrueFalse(trueFalseQuestions);
                model.addAttribute("questionSetDTO", questionSetDTO);
                model.addAttribute("questionSetID", questionSet.getId());
                model.addAttribute("trueFalseNum", questionSet.getTrueFalseQuestions().size());
                model.addAttribute("priorityListNum", questionSet.getPriorityListQuestions().size());
                model.addAttribute("multiChoiceNum", questionSet.getMultipleChoiceQuestions().size());
                return "convenor/CreateQuestionSet";
            }
        }
        request.getSession().setAttribute("Error", "The question set does not exist or you do not have permission to remove this question set");
        return "redirect:/convenor/question-sets/";
    }

    @GetMapping("/question-set/preview/{id}")
    public String viewQuestionSet(Model model, @PathVariable int id, Principal principal, HttpServletRequest request){

        List<QuestionSet> questionSets = getConvenorsQuestionSets(principal.getName(), userRepo, questionSetRepo);
        for (QuestionSet questionSet : questionSets) {
            if (questionSet.getId() == id) {
                PreferenceSet preferenceSet = new PreferenceSet();
                preferenceSet.setName("QUESTION SET PREVIEW");
                preferenceSet.setDeadline(LocalDateTime.parse("2222-01-01T00:00"));
                preferenceSet.setGroupMinNumber(10);
                preferenceSet.setGroupMaxNumber(15);
                Module module = new Module();
                module.setCode("COOOO1");
                preferenceSet.setModule(module);
                preferenceSet.setQuestionSet(questionSet);
                preferenceSet.setStatus(PreferenceSetStatus.AWAITING_DEADLINE);
                model.addAttribute("preferenceSet", preferenceSet);
                model.addAttribute("preview", true);
                model.addAttribute("studentAnswerDTO", new StudentAnswerDTO());
                return "convenor/answer-preference-set-preview";
            }
        }
        request.getSession().setAttribute("Error", "The question set does not exist or you do not have permission to remove this question set");
        return "redirect:/convenor/question-sets/";
    }

    /**
     * Process and store the new/updated question set in the database
     */
    @PostMapping(value = {"/question-set/{id}", "/question-set/"})
    public String createQuestionSet(@Valid @ModelAttribute("questionSetDTO") QuestionSetDTO questionSetDTO, BindingResult result,
                                    @PathVariable Optional<String> id,
                                    Principal principal,
                                    HttpServletRequest request, Model model) {
        QuestionSet questionSet = null;

        if (result.hasErrors()) {
            MainController.errorMessage(model, "Please check the errors below and try again");
            id.ifPresent(s -> model.addAttribute("questionSetID", s));
            return "convenor/CreateQuestionSet";
        }

        if (id.isPresent()) { //check if we are trying to update a question set
            User user = userRepo.findByEmail(principal.getName());
            Convenor convenor = (Convenor) user;
            List<QuestionSet> currentQuestionSets = questionSetRepo.findQuestionSetByConvenor(convenor);
            try {
                for (QuestionSet currentQuestionSet : currentQuestionSets) {
                    if (currentQuestionSet.getId() == Integer.parseInt(id.get())) { //if question set already exists
                        questionSet = currentQuestionSet; //update the existing question set
                        break;
                    }
                }
                if (questionSet == null) { // could not find the question set for the convenor
                    questionSet = new QuestionSet();
                }
            } catch (Exception e) { // could not convert the path variable to an Integer
                questionSet = new QuestionSet();
            }

        } else {
            questionSet = new QuestionSet();
        }
        List<TrueFalse> trueFalseQuestions = new ArrayList<>();
        List<PriorityList> priorityListQuestions = new ArrayList<>();
        List<MultipleChoice> multipleChoiceQuestions = new ArrayList<>();

        for (TrueFalseDTO trueFalseQuestionDTO : questionSetDTO.getTrueFalse()) {
            TrueFalse trueFalseQuestion = new TrueFalse();
            trueFalseQuestion.setQuestionText(trueFalseQuestionDTO.getQuestionText());
            trueFalseQuestion.setQuestionDescription(trueFalseQuestionDTO.getQuestionDescription());
            trueFalseQuestion.setGroupAnswersBySimilar(Boolean.parseBoolean(trueFalseQuestionDTO.getGroupAnswersBySimilar()));
            trueFalseQuestion.setQuestionWeight(Integer.parseInt(trueFalseQuestionDTO.getQuestionWeight()));
            trueFalseQuestion.setQuestionOrder(0);
            trueFalseQuestion.setOption1(trueFalseQuestionDTO.getOption1());
            trueFalseQuestion.setOption2(trueFalseQuestionDTO.getOption2());
            trueFalseQuestions.add(trueFalseQuestion);
        }

        for (PriorityListDTO priorityListDTO : questionSetDTO.getPriorityList()) {
            PriorityList priorityListQuestion = new PriorityList();
            priorityListQuestion.setQuestionText(priorityListDTO.getQuestionText());
            priorityListQuestion.setQuestionDescription(priorityListDTO.getQuestionDescription());
            priorityListQuestion.setGroupAnswersBySimilar(Boolean.parseBoolean(priorityListDTO.getGroupAnswersBySimilar()));
            priorityListQuestion.setQuestionWeight(Integer.parseInt(priorityListDTO.getQuestionWeight()));
            priorityListQuestion.setQuestionOrder(0);
            for (ChoiceOptionDTO choicesDTO : priorityListDTO.getOptions()) {
                ChoiceOption option = new ChoiceOption();
                option.setChoiceName(choicesDTO.getChoiceName());
                priorityListQuestion.addOption(option);
            }
            priorityListQuestions.add(priorityListQuestion);
        }

        for (MultipleChoiceDTO multipleChoiceDTO : questionSetDTO.getMultipleChoice()) {
            MultipleChoice multipleChoiceQuestion = new MultipleChoice();
            multipleChoiceQuestion.setQuestionText(multipleChoiceDTO.getQuestionText());
            multipleChoiceQuestion.setQuestionDescription(multipleChoiceDTO.getQuestionDescription());
            multipleChoiceQuestion.setGroupAnswersBySimilar(Boolean.parseBoolean(multipleChoiceDTO.getGroupAnswersBySimilar()));
            multipleChoiceQuestion.setQuestionWeight(Integer.parseInt(multipleChoiceDTO.getQuestionWeight()));
            multipleChoiceQuestion.setQuestionOrder(0);
            multipleChoiceQuestion.setMinimumSelection(Integer.parseInt(multipleChoiceDTO.getMinimumSelection()));
            multipleChoiceQuestion.setMaximumSelection(Integer.parseInt(multipleChoiceDTO.getMaximumSelection()));
            List<ChoiceOption> choices = multipleChoiceDTO.getOptions();
            multipleChoiceQuestion.setOptions(choices);
            multipleChoiceQuestions.add(multipleChoiceQuestion);
        }

        questionSet.setName(questionSetDTO.getName());
        questionSet.setStudentChooseOwnGroup(Boolean.parseBoolean(questionSetDTO.getStudentChooseOwnGroup()));
        questionSet.setNumQuestions(multipleChoiceQuestions.size() + priorityListQuestions.size() + trueFalseQuestions.size());
        questionSet.setMultipleChoiceQuestions(multipleChoiceQuestions);
        questionSet.setPriorityListQuestions(priorityListQuestions);
        questionSet.setTrueFalseQuestions(trueFalseQuestions);
        questionSet.setConvenor((Convenor) userRepo.findByEmail(principal.getName()));
        questionSetRepo.save(questionSet);

        request.getSession().setAttribute("message", "Success, you have created/updated a question set.");
        return "redirect:/convenor/question-sets";
    }


}
