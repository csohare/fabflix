let searchForm = $("#search_form");



function submitForm(formSubmitEvent) {
    console.log("FORM SUBMITTED");

    let movieTitle = $("#movie_title").val();
    let movieDirector =  $("#director").val();
    let movieYear = $("#year").val();
    let starName = $("#star_name").val();
    let url = "./movieList.html?movieTitle=" + encodeURIComponent(movieTitle)+ "&director=" + encodeURIComponent(movieDirector)+ "&year=" + encodeURIComponent(movieYear) + "&starName=" + encodeURIComponent(starName) + "&pageSize=25&pageOffset=0";
    window.location.replace(url);

}

searchForm.submit(submitForm);
