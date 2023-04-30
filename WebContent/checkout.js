let checkoutForm = $("#checkout_form");


function handleCheckoutRequest(result) {
    
}


function checkoutSubmit(event) {
    event.preventDefault();
    $.ajax("api/checkout", {
            method: "POST",
            dataType: "json",
            data:  loginForm.serialize(),
            success: handleCheckoutRequest
        }
    );
}

checkoutForm.submit(checkoutSubmit);