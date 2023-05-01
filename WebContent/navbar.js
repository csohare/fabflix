let searchForm = $("#search_form");
let movieList = $('.movieList');

function handleJump(resultData){
    console.log(resultData["query"]);
    console.log(window.location.origin)
    console.log(window.location.pathname)
    let newURL = window.location.origin +  "/PROJECT/movieList.html?" + resultData["query"];
    window.location.replace(newURL);
    console.log(newURL);
}

function submitForm(formSubmitEvent) {
    console.log("FORM SUBMITTED");

    let movieTitle = $("#movie_title").val();
    let movieDirector =  $("#director").val();
    let movieYear = $("#year").val();
    let starName = $("#star_name").val();
    let url = "./movieList.html?movieTitle=" + encodeURIComponent(movieTitle)+ "&director=" + encodeURIComponent(movieDirector)+ "&year=" + encodeURIComponent(movieYear) + "&starName=" + encodeURIComponent(starName) + "&pageSize=25&pageOffset=0&sort=1";
    window.location.replace(url);

}

function movieListReturn(event) {
    event.preventDefault();
    console.log("PRESSED");
    $.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/listJump",
        success: handleJump
    });
}

movieList.click(movieListReturn);
searchForm.submit(submitForm);
