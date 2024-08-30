$(function () {
    let minGroupNumber = $('#group-min-number').val();
    let maxGroupNumber = $('#group-max-number').val();
    let container = $(".container");

    $(".add-member").on('click', function () {
        $(renderMemberEmailInput()).insertAfter($('.form-group.member-email').last());
        updateMemberNumbers()
        checkGroupSize(minGroupNumber, maxGroupNumber)
    });

    $(container).on('click', '.close',function (){
        $(this).closest($('.member-email')).remove();
        updateMemberNumbers()
        checkGroupSize(minGroupNumber, maxGroupNumber)
    });

    checkGroupSize(minGroupNumber, maxGroupNumber)

    //Order the list items based on the input value (the order they specified before)
    $('.list-group').each(function () {
       $(this).find('.list-group-item').sort(function (a, b) {
           return + $(a).children('input').val() - +$(b).children('input').val()
       }).appendTo($(this));
    });

    $('.sortable').each(function () {
        $(this).sortable(
            {cursor: "grabbing"}
        );
    })

    $("input.form-check-input:checkbox").on("change", function () {
        if($(this).closest('.options').find(":checkbox:checked").length > parseInt($(this).closest(".card-body").find('.multiple-choice-max').val())) {
            // noinspection JSUnusedGlobalSymbols
            this.checked = false;
        }
    });

    $(".bi-chevron-up").on("click", function () {
        let $currentItem = $(this).closest(".list-group-item");
        let $previousItem = $currentItem.prev(".list-group-item");
        if ($previousItem.length !== 0){
            $currentItem.insertBefore($previousItem)
        }
        return false;
    });

    $(".bi-chevron-down").on("click", function (){
        let $currentItem = $(this).closest(".list-group-item");
        let $nextItem = $currentItem.next(".list-group-item");
        if ($nextItem.length !== 0){
            $currentItem.insertAfter($nextItem)
        }
        return false;
    })

    $("#student-answer-form").on('submit', function () {
        $(".list-group").each(function() {
            let orderNum = 0;
            $(this).children('li.list-group-item').children('input').each(function(){
                $(this).attr("value", orderNum)
                orderNum++
            });
        });
        let i = 0;
        $("input.member-email").each(function () {
            $(this).attr("name", "preferredGroupMembers["+i+"]");
            i ++;
        })
    });
});

function renderMemberEmailInput() {
    return `
    <div class="form-group my-3 member-email">
        <div class="input-group">
            <label for="groupMemberEmail_" class="w-100">Other group member</label>
            <input id="groupMemberEmail_" class="form-control member-email" placeholder="#">
            <button class="btn btn-outline-primary close" type="button"><i class="bi bi-x-lg"></i></button>
        </div>
    </div>
    `
}

function updateMemberNumbers(){
    let i = 2;
    $('input.member-email').each(function () {
        $(this).closest('.form-group').find('label').attr('for', 'groupMemberEmail_'+i).html("Other group member "+i+":");
        $(this).attr('id', 'groupMemberEmail_'+i).attr("placeholder", "#"+i);
        i ++;
    })

}

function checkGroupSize(min, max){
    if (min === max){
        removeAddMemberButton()
        return;
    }
    let memberEmail = $("input.member-email");

    if (memberEmail.length < parseInt(max) && $('.add-member').hasClass("d-none")){
        addMemberButton();
    }

    if (memberEmail.length === parseInt(max)-1){
        removeAddMemberButton();
    }
}

function removeAddMemberButton(){
    $(".add-member").addClass("d-none")
}

function addMemberButton(){
    $(".add-member").removeClass("d-none")
}