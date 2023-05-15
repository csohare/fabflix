let starForm = $("#starForm");
let inserted = $("#inserted");
function handleStarInsert(data) {
    rowHTML = "";
    rowHTML = "<p>INSERTED STAR WITH ID " + data["id"] + "</p>";
    inserted.append(rowHTML);

}
function handleSubmit(event) {
    console.log("FORM SUBMITTED");
    event.preventDefault();

    $.ajax("api/addstar", {
            method: "GET",
            dataType: "json",
            data:  starForm.serialize(),
            success: handleStarInsert
        }
    );

}

starForm.submit(handleSubmit);