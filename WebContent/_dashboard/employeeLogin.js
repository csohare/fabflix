let employeeLogin = $('#employeelogin_form');
let errorMessage = $('#login-fail')

function handleLoginRequest(data) {
    console.log(data["status"]);
    if(data["status"] === "success") {
        window.location.replace("./metaData.html")
    }
    else {
        errorMessage.text(data["message"]);
    }

}

function submitForm(event) {
    console.log("FORM SUBMITTED");
    event.preventDefault();
    $.ajax("api/employeelogin", {
            method: "POST",
            dataType: "json",
            data:  employeeLogin.serialize(),
            success: handleLoginRequest
        }
    );
}

employeeLogin.submit(submitForm);