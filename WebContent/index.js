let genreTableBody = $('#genre-table-body');
let titleTableBody = $('#title-table-body');

function handleResult(resultData) {
    console.log(resultData.length);
    let rowHTML = "<tr>"
    for(let i = 0; i < resultData.length; ++i) {
        if(i != 0 && !(i % 4))  {rowHTML += "</tr>" + "<tr>"}
        rowHTML += "<th>" +
            '<a href="movieList.html?movieGenre=' + resultData[i]["id"] + '">'
            + resultData[i]["genre"] + "</a></th>";
    }
    rowHTML += "</tr>";
    genreTableBody.append(rowHTML);
}

function populateTitles(){
    let rowHTML = "<tr>";
    for(let i = 65; i <= 90; ++i) {
        rowHTML += "<th>";
        rowHTML += '<a href="movieList.html?movieTitle=' + String.fromCharCode(i) + '">'
        + String.fromCharCode(i) + " </a></th>";
    }
    rowHTML += "</tr>";
    rowHTML += "<tr>";
    for(let i = 48; i < 58; ++i) {
        rowHTML += "<th>";
        rowHTML += '<a href="movieList.html?movieTitle=' +  String.fromCharCode(i) + '">'
        + String.fromCharCode(i) + " </a></th>";
    }
    titleTableBody.append(rowHTML);
}

jQuery.ajax({
    url: "api/index",
    method: "GET",
    dataType: "json",
    success: (resultData) => handleResult(resultData)
});
populateTitles();


