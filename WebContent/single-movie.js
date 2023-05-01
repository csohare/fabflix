/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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
    let jsonIds = resultData[0]["movie_starIds"];
    let jsonNames = resultData[0]["movie_starNames"];
    let movie_starsIds = jsonIds.split(",");
    let movie_starsNames = jsonNames.split(",");

    let gIds = resultData[0]["movie_genreIds"];
    let gNames = resultData[0]["movie_genre"];
    let movie_genreIds = gIds.split(",");
    let movie_genreNames = gNames.split(",");

    console.log("handleResult: populating star info from resultData");
    let movieNameElement = jQuery("#movie_name");
    let movieName = resultData[0]["movie_title"];
    let movieTableBodyElement = jQuery("#single_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[0]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[0]["movie_director"] + "</th>";
        rowHTML += "<th>";
        for(let i = 0; i < movie_genreIds.length; i++) {
            rowHTML += '<a href="movieList.html?movieGenre=' + movie_genreIds[i] + "&pageSize=25&pageOffset=0&sort=1" +
                       '">' + movie_genreNames[i] + " </a>";
        }
        rowHTML += "<th>";
        for(let i  = 0; i < movie_starsIds.length; i++)
        {
            console.log(movie_starsNames[i]);
            rowHTML += '<a href="single-star.html?id=' + movie_starsIds[i] + '">' + movie_starsNames[i];
            if(i!= movie_starsIds.length - 1) {rowHTML += ", ";}
            rowHTML += "</a>";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[0]["movie_rating"] + "</th>";
        rowHTML += "<th>" + '<button type="button" class="btn btn-outline-success cartButton" data-value="' + resultData[0]["movieId"] + '">' +
        "Add to Cart</button></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
        movieNameElement.append(movieName);
}
function addToCart(event) {
    event.preventDefault();
    let data = $(this).data("value");
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/CartAdd?movieId=" + data +"&action=0", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: function(resultData) {
            alert("Added to cart");
        }
    });

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');
$(document).on('click', '.cartButton', addToCart);

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});