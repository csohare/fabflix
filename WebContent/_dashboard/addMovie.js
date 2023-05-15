let movieForm = $("#movieForm");
let inserted = $('#inserted');

function handleMovieInsert(data) {
    if(data["movieId"] == "-1") {
        inserted.append("<p>Duplicate Movie Entry</p>");
    }
    else{
        let rowHTML = "";
        rowHTML += "<p>Inserted| MovieId: " + data["movieId"] + " starId: " + data["starId"] + " genreId: " + data["genreId"] + "</p>";
        inserted.append(rowHTML);
    }
}

function handleSubmit(event) {
    event.preventDefault();
    console.log("SUBMITTED");

    $.ajax("api/addmovie", {
            method: "GET",
            dataType: "json",
            data:  movieForm.serialize(),
            success: handleMovieInsert
        }
    );
}

movieForm.submit(handleSubmit);