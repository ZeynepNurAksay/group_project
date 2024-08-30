$(function () {
    let trueFalseNum = $("#trueFalseNum").val();
    let priorityListNum = $("#priorityListNum").val();
    let priorityListOptionIndex = 1000;
    let multiChoiceNum = $("#multiChoiceNum").val();
    let multiChoiceOptionIndex = 1000;
    let container = $(".container");
    let questionSetForm = $("#questionSetForm")

    let formAction = questionSetForm.attr("action");
    let formActionComponents = formAction.split("/")
    if (formActionComponents[formActionComponents.length -1] !== ""){
        $(".submit-btn").html("Update Question Set")
    }


    $(".add-question-type").click(function () {
        let html = "";
        switch ($(".question-type").find(":selected").attr("value")) {
            case "true-false":
                html = renderTrueFalse(trueFalseNum)
                trueFalseNum += 1
                break;
            case "priority-list":
                html = renderPriorityList(priorityListNum, priorityListOptionIndex)
                priorityListNum += 1
                break;
            case "multi-choice":
                html = renderMultiChoice(multiChoiceNum, multiChoiceOptionIndex)
                multiChoiceNum += 1;
        }
        let questions = $(".question");
        if (questions[0]) { // If there is already a question, insert the new question after the last question
            $(html).insertAfter($(questions[questions.length - 1]));
        } else {
            $(html).insertAfter($(".student-grouping"));
        }
    });

    container.on('click', '.remove-question', function () {
        $(this).closest(".question").remove();
    });

    container.on('click', '.add-option', function () {
        let html = addOption($(this).attr("data-item-index"), priorityListOptionIndex, $(this).attr("data-group-name"));
        priorityListOptionIndex += 1;
        let lastQuestion = $(this).closest(".question");
        let lastOption = lastQuestion.find(".option")[lastQuestion.find(".option").length - 1]
        $(html).insertAfter(lastOption);
    });

    container.on('click', ".remove-option", function () {
        $(this).closest(".option").remove();
    })

    //Change the selected attribute of a select element when a user selects a different value
    $(document).on("change", "select", function () {
        $("option[value=" + this.value + "]", this)
            .attr("selected", true).siblings()
            .removeAttr("selected")
    });

    questionSetForm.submit(function () {
        let trueFalseCount = 0;
        //set the array indexes before submitting the form
        $(".trueFalse").each(function () {
            $(this).find(".true-false-input").each(function () {
                let existingName = $(this).attr("name"); //get the existing attribute to add to
                existingName = existingName.substring(existingName.indexOf(".")) //remove any uniqueIDs before the radio button groups
                $(this).attr("name", "trueFalse[" + trueFalseCount + "]" + existingName); //set the object to post to
            });
            trueFalseCount += 1;
        });

        let multiChoiceCount = 0;

        $(".multiChoice").each(function () {
            $(this).find(".multi-choice-input").each(function () {
                let existingName = $(this).attr("name");
                existingName = existingName.substring(existingName.indexOf("."))
                $(this).attr("name", "multipleChoice[" + multiChoiceCount + "]" + existingName)
            });
            let multiChoiceOptionCount = 0;
            $(this).find(".option-input").each(function () {
                $(this).attr("name", "multipleChoice[" + multiChoiceCount + "].options[" + multiChoiceOptionCount + "].choiceName")
                multiChoiceOptionCount += 1;
            });
            multiChoiceCount += 1
        });

        let priorityListCount = 0;

        $(".priorityList").each(function () {
            $(this).find(".priority-list-input").each(function () {
                let existingName = $(this).attr("name");
                existingName = existingName.substring(existingName.indexOf("."))
                $(this).attr("name", "priorityList[" + priorityListCount + "]" + existingName)
            });
            let priorityListOptionCount = 0;
            $(this).find(".option-input").each(function () {
                $(this).attr("name", "priorityList[" + priorityListCount + "].options[" + priorityListOptionCount + "].choiceName")
                priorityListOptionCount += 1;
            });
            priorityListCount += 1
        });
    });
});

function addOption(index, optionIndex, groupString) {
    return `
    <div class="form-group mb-3 option">
        <div class="input-group">
            <label for="` + index + `_` + groupString + `-option_` + optionIndex + `" class="w-100">Option</label>
            <input id="` + index + `_` + groupString + `-option_` + optionIndex + `" name="" class="form-control option-input" placeholder="Enter option text"/>
            <button class="btn btn-outline-primary close remove-option" type="button"><i class="bi bi-x-lg"></i></button>
        </div>
    </div>
    `
}

function renderMultiChoice(index, optionIndex) {
    return `
        <div class="row my-3 question multiChoice ` + index + `_multiChoice">
            <div class="col-lg-12">
                <div class="card">
                    <div class="card-body">
                        <div class="form-group mb-3">
                            <h3 class="mb-3">Multiple / Single Choice Question</h3>
                            <p>Allow students to choose multiple options</p>
                            <div class="form-check">
                                <label for="` + index + `_multi-choice-booleanSimilar" class="form-check-label">Group students that have selected the same option</label>
                                <input type="radio" id="` + index + `_multi-choice-booleanSimilar" name="multi_` + index + `.groupAnswersBySimilar" value="true" class="form-check-input multi-choice-input">
                            </div>
                            <div class="form-check mb-3">
                                <label for="` + index + `_multi-choice-booleanDifferent" class="form-check-label">Group students that have selected
                                    different options</label>
                                <input type="radio" id="` + index + `_multi-choice-booleanDifferent" name="multi_` + index + `.groupAnswersBySimilar" value="false" class="form-check-input multi-choice-input">
                            </div>
                            <div class="form-group mb-3">
                                <label for="` + index + `_multi-choice-questionText">Question Text:</label>
                                <input id="` + index + `_multi-choice-questionText" type="text" class="form-control multi-choice-input" name=".questionText"
                                   placeholder="Enter the question to ask your students:"/>
                            </div>
                            
                            <div class="form-group mb-3">
                                <label for="` + index + `_multi-choice_desc">Question Description: (Optional)</label>
                                <input id="` + index + `_multi-choice_desc" type="text" class="form-control multi-choice-input"
                                   name=".questionDescription"
                                   placeholder="Add any instructions to your students here"/>
                            </div>
                            
                            <div class="form-group mb-3">
                                <label for="` + index + `_multiChoiceAmountMax">Enter the maximum number of options a student can select</label>
                                <input id="` + index + `_multiChoiceAmountMax" type="number" class="form-control multi-choice-input" name=".maximumSelection">
                            </div>
                            
                            <div class="form-group mb-3">
                                <label for="` + index + `_multiChoiceAmountMinimum">Enter the minimum number of options a student can select</label>
                                <input id="` + index + `_multiChoiceAmountMinimum" type="number" class="form-control multi-choice-input" name=".minimumSelection">
                            </div>
                            
                            <div class="form-group mb-3 option">
                                <label for="` + index + `_multi-choice-option_0">Option</label>
                                <input id="` + index + `_multi-choice-option_0" name=".options[` + optionIndex + `].choiceName" class="form-control option-input" placeholder="Enter option text"/>
                            </div>

                            <div class="col-12 col-md-6">
                                <div class="form-group mb-4">
                                    <button class="btn btn-outline-primary add-option w-100 mt-3" data-item-index="` + index + `" data-group-name="multi-choice" type="button">Add option</button>
                                </div>
                            </div>
                            
                            
                            <div class="form-group mb-3">
                                <label for="` + index + `_multi-choice_weight">Weight (1-10)</label>
                                <input id="` + index + `_multi-choice_weight" type="number" name=".questionWeight"
                                   class="form-control multi-choice-input" placeholder="How important is this question when grouping students?"/>
                            </div>      
                            
                            <div class="col-12 col-md-6">                                   
                                <button class="btn btn-secondary remove-question w-100 mt-3" data-index="` + index + `" type="button">
                                <i class="bi bi-x-circle-fill text-light"></i> Remove Question
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>  
    `
}

function renderPriorityList(index, optionIndex) {
    return `
    <div class="row my-3 question priorityList ` + index + `_priorityList">
            <div class="col-lg-12">
                <div class="card">
                    <div class="card-body">
                        <div class="form-group mb-3">
                            <h3 class="mb-3">Priority List</h3>
                            <p>Create a list for students to order their preferences based on the options you give them:</p>
                            <div class="form-check">
                                <label for="` + index + `_priority-list-booleanSimilar" class="form-check-label">Group students that have selected the same option</label>
                                <input type="radio" id="` + index + `_priority-list-booleanSimilar" name="priorityList_` + index + `.groupAnswersBySimilar"
                                       value="true" class="form-check-input priority-list-input">
                            </div>
                            <div class="form-check mb-3">
                                <label for="` + index + `_priority-list-booleanDifferent" class="form-check-label">Group students that have selected
                                    different options</label>
                                <input type="radio" id="` + index + `_priority-list-booleanDifferent" name="priorityList_` + index + `.groupAnswersBySimilar"
                                       value="false" class="form-check-input priority-list-input">

                            </div>
                            <div class="form-group mb-3">
                                <label for="` + index + `_priority-list-questionText">Question Text:</label>
                                <input id="` + index + `_priority-list-questionText" type="text" class="form-control priority-list-input" name=".questionText"
                                   placeholder="Enter the question to ask your students:"/>
                            </div>
                            
                            <div class="form-group mb-3">
                                <label for="` + index + `_priority-list_desc">Question Description: (Optional)</label>
                                <input id="` + index + `_priority-list_desc" type="text" class="form-control priority-list-input"
                                   name=".questionDescription"
                                   placeholder="Add any instructions to your students here"/>
                            </div>
                            
                            <div class="form-group mb-3 option">
                                <label for="` + index + `_priority-list-option_0">Option</label>
                                <input id="` + index + `_priority-list-option_0" name=".options[` + optionIndex + `].choiceName" class="form-control option-input" placeholder="Enter option text"/>
                            </div>

                            <div class="col-12 col-md-6">
                                <div class="form-group mb-4">
                                    <button class="btn btn-outline-primary add-option mt-3 w-100" data-item-index="` + index + `" type="button">Add option</button>
                                </div>
                            </div>
                            
                            <div class="form-group mb-3">
                                <label for="` + index + `_priority-list_weight">Weight (1-10)</label>
                                <input id="` + index + `_priority-list_weight" type="number" name=".questionWeight"
                                   class="form-control priority-list-input" placeholder="How important is this question when grouping students?"/>
                            </div>      
                                          
                            <div class="col-12 col-md-6">                     
                                <button class="btn btn-secondary remove-question w-100 mt-3" data-index="` + index + `" type="button">
                                <i class="bi bi-x-circle-fill text-light"></i> Remove Question
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `
}

function renderTrueFalse(index) {
    return `
    <div class="row my-3 question trueFalse ` + index + `_trueFalse">
            <div class="col-lg-12">
                <div class="card">
                    <div class="card-body">
                        <div class="form-group mb-3">
                            <h3 class="mb-3">True False</h3>
                            <p>Choose how to group students:</p>
                            <div class="form-check">
                                <label for="` + index + `_true_false_booleanSimilar" class="form-check-label">Group students that have selected the same option</label>
                                <input type="radio" id="` + index + `_true_false_booleanSimilar" name="TF_` + index + `.groupAnswersBySimilar"
                                       value="true" class="form-check-input true-false-input">
                            </div>
                            <div class="form-check mb-3">
                                <label for="` + index + `_true_false_booleanDifferent" class="form-check-label">Group students that have selected
                                    different options</label>
                                <input type="radio" id="` + index + `_true_false_booleanDifferent" name="TF_` + index + `.groupAnswersBySimilar"
                                       value="false" class="form-check-input true-false-input">

                            </div>
                            <div class="form-group mb-3">
                                <label for="` + index + `_true_false_questionText">Question Text:</label>
                                <input id="` + index + `_true_false_questionText" type="text" class="form-control true-false-input" name=".questionText"
                                   placeholder="Enter the question to ask your students:"/>
                            </div>
                            
                            <div class="form-group mb-3">
                                <label for="` + index + `_true_false_desc">Question Description: (Optional)</label>
                                <input id="` + index + `_true_false_desc" type="text" class="form-control true-false-input"
                                   name=".questionDescription"
                                   placeholder="Add any instructions to your students here"/>
                            </div>
                            

                            <div class="form-group mb-3">
                                <label for="` + index + `_true_false_1">Option 1</label>
                                <input id="` + index + `_true_false_1" name=".option1" class="form-control true-false-input" placeholder="Enter option 1 text"/>
                            </div>

                            <div class="form-group mb-3">
                                <label for="` + index + `_true_false_2">Option 2</label>
                                <input id="` + index + `_true_false_2" name=".option2" class="form-control true-false-input" placeholder="Enter option 2 text"/>
                            </div>
                            
                            <div class="form-group mb-3">
                                <label for="` + index + `_true_false_weight">Weight (1-10)</label>
                                <input id="` + index + `_true_false_weight" type="number" name=".questionWeight"
                                   class="form-control true-false-input" placeholder="How important is this question when grouping students?"/>
                            </div>      
                            
                            <div class="col-12 col-md-6">
                                <button class="btn btn-secondary remove-question w-100 mt-3" data-index="` + index + `" type="button">
                                <i class="bi bi-x-circle-fill text-light"></i> Remove Question
                                </button>
                            </div>                                   
                            
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}