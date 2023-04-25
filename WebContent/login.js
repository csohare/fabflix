let loginForm = $("#login_form");


function handleLoginRequest(resultData) {
    console.log("HANDLING LOGIN REQUEST");
    console.log(resultData);

    if(resultData["status"] === "success") {
        window.location.replace("index.html");
    }
    else{
        $("#login-fail").text(resultData["message"]);
    }
}
function submitLoginForm(formSubmitEvent) {
    console.log("FORM SUBMITTED");

    formSubmitEvent.preventDefault();

    $.ajax("api/login", {
            method: "POST",
            dataType: "json",
            data:  loginForm.serialize(),
            success: handleLoginRequest
        }
    );
}
loginForm.submit(submitLoginForm);