let checkoutForm = $("#checkout_form");
let errorText = $('#error_text');
let totalCheckout = $("#total_checkout");


function handleCheckoutRequest(result) {
    if(result["status"] == "failed") {
        errorText.text(result["message"]);
    }
    window.location.reload();
}

function handleResult(resultData) {
    let formButton = $("#submit_btn");
    totalCheckout.append(resultData["total"]);
    if(resultData["total"] === "0")  {console.log("0"); formButton.prop("disabled", true);}
}


function checkoutSubmit(event) {
    event.preventDefault();
    $.ajax("api/checkout", {
            method: "POST",
            dataType: "json",
            data:  checkoutForm.serialize(),
            success: handleCheckoutRequest
        }
    );
}

checkoutForm.submit(checkoutSubmit);

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/checkout",
    success: (resultData) => handleResult(resultData)
});