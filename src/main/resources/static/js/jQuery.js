$(function () {
    //Get the URL at the address bar excluding parameters
    let url = (window.location.href).split("?")[0]

    $(".header-link").each(function () {
        if (url === (this.href)){
            $(this).closest("a").addClass("active");
            //$(this).closest(".dropdown").children(".nav-link").addClass("nav-link-active");
        }
    })

    //Keep the footer at the bottom of the page if there is less content and no scroll bar
    setFooterStyle()
    window.onresize = setFooterStyle;

    //See if the user is logged in, if so replace the login button with the logout button
    $.ajax({
        'url': '/user/isLoggedIn/',
        'success': function (response){
            if (response) {
                $('#login-btn').addClass("d-none")
                $('#logout-btn').removeClass("d-none")
                $('#sign-up').html("Profile Settings").attr("href", "/profile")
            }
        }
    })
})

function setFooterStyle(){
    const footer = $("#footer");
    const docHeight = $(window).height();
    const footerHeight = footer.outerHeight();
    const footerTop = footer.position().top + footerHeight;
    if (footerTop < docHeight){
        footer.css('margin-top', (docHeight - footerTop) + 'px');
    } else {
        footer.css('margin-top', '');
    }
    footer.removeClass('invisible')
}