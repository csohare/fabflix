
let movieTableBody = jQuery("#movie_table_body");
let pageSize = $('.pageSize');
let paginationButton = $('.page_btn');
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {

    movieTableBody.empty();
    for(let i = 0; i < resultData.length; ++i) {
        let rowHTML = "";
        let sNames= resultData[i]["starNames"];
        let sIds = resultData[i]["starIds"];
        let gNames = resultData[i]["genreNames"];
        let gIds = resultData[i]["genreIds"];
        const starNames = sNames.split(",");
        const starIds = sIds.split(",");
        const genreNames = gNames.split(",");
        const genreIds = gIds.split(",");

        rowHTML += "<tr>";

        rowHTML += "<th>"
            + "<a href='single-movie.html?id=" + resultData[i]["movieId"] + "'>"
            + resultData[i]["title"] + "</a></th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";

        rowHTML+= "<th>"
        for(let j = 0; j < genreNames.length && j < 3; ++j) {
            rowHTML += "<a href='movieList.html?movieGenre=" + genreIds[j] + "'>"
                + genreNames[j] + " </a>";
        }
        rowHTML += "</th>";

        rowHTML += "<th>";
        for(let j = 0; j < starNames.length; ++j) {
            rowHTML += "<a href='single-star.html?id=" + starIds[j] + "'>"
                + starNames[j] + " </a>";
        }
        rowHTML += "</th>";

        let rating = resultData[i]["rating"] == null ? "N/A" : resultData[i]["rating"];
        rowHTML += "<th>" + rating + "</th>";
        rowHTML += "</tr>"
        movieTableBody.append(rowHTML);
    }
}

function genURL(pSize) {
    let url = "api/MovieList?";
    let pageSize = getParameterByName("pageSize");
    let pageOffset = getParameterByName("pageOffset");
    if(pageSize == null)    {pageSize = "25";}
    if(pageOffset == null)  {pageOffset = "0";}

    if(getParameterByName('movieGenre') != null) {
        let movieGenre = getParameterByName('movieGenre');
        url += "movieGenre=" + movieGenre;
    }

    if(getParameterByName('movieTitle') != null) {
        let movieTitle = getParameterByName('movieTitle');
        url += "movieTitle=" + movieTitle;
    }

    if(getParameterByName('director') != null) {
        let director = getParameterByName('director');
        url += "&director=" + director;
    }

    if(getParameterByName('year') != null) {
        let year = getParameterByName('year');
        url += "&year=" + year;
    }
    if(getParameterByName('starName') != null) {
        let starName = getParameterByName('starName');
        if(starName != "") {url += "&starName=" + starName;}
    }
    if(pSize != "") {pageSize = pSize;}
    url += "&pageSize=" + pageSize + "&pageOffset=" + pageOffset;
    return url;
}

function changePageSize(event) {
    event.preventDefault();
    let pageSize = $(this).text();
    let urlSearchParams = new URLSearchParams(window.location.search);

    urlSearchParams.set("pageSize", pageSize);
    urlSearchParams.set("pageOffset", "0");
    let updatedQuery = urlSearchParams.toString();
    let newURL = window.location.origin + window.location.pathname + "?" + updatedQuery;

    window.location.replace(newURL);


}
function pagination(event) {
    event.preventDefault();
    const size = parseInt(getParameterByName("pageSize"));
    let tmp= parseInt(getParameterByName("pageOffset"));
    if($(this).text() == "Prev") {tmp -= size;}
    if($(this).text() == "Next") {tmp += size;}
    const updatedOffset= tmp.toString();

    let urlSearchParams = new URLSearchParams(window.location.search);
    urlSearchParams.set("pageOffset", updatedOffset);
    let updatedQuery = urlSearchParams.toString();
    let newURL = window.location.origin + window.location.pathname + "?" + updatedQuery;
    window.location.replace(newURL);

}

function onLoad() {
        let prevButton = $("#prev");
        if(getParameterByName("pageOffset") === "0") {
            prevButton.prop("disabled", true);
        }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
onLoad();
pageSize.click(changePageSize);
paginationButton.click(pagination);



let URL = genURL("");


// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: URL,
    success: (resultData) => handleResult(resultData)
});