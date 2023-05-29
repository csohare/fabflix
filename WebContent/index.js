let genreTableBody = $('#genre-table-body');
let titleTableBody = $('#title-table-body');

function handleResult(resultData) {
    let rowHTML = "<tr>"
    for(let i = 0; i < resultData.length; ++i) {
        if(i != 0 && !(i % 4))  {rowHTML += "</tr>" + "<tr>"}
        rowHTML += "<th>" +
            '<a href="movieList.html?movieGenre=' + resultData[i]["id"] + '&pageSize=25&pageOffset=0&sort=1">'
            + resultData[i]["genre"] + "</a></th>";
    }
    rowHTML += "</tr>";
    genreTableBody.append(rowHTML);
}

function populateTitles(){
    let rowHTML = "<tr>";
    for(let i = 65; i <= 90; ++i) {
        rowHTML += "<th>";
        rowHTML += '<a href="movieList.html?movieTitle=' + String.fromCharCode(i) + '&pageSize=25&pageOffset=0&sort=1">'
        + String.fromCharCode(i) + " </a></th>";
    }
    rowHTML += "</tr>";
    rowHTML += "<tr>";
    for(let i = 48; i < 58; ++i) {
        rowHTML += "<th>";
        rowHTML += '<a href="movieList.html?movieTitle=' +  String.fromCharCode(i) + '">'
        + String.fromCharCode(i) + " </a></th>";
        }
    rowHTML += '<th><a href="movieList.html?movieTitle=' + '*' + '&pageSize=25&pageOffset=0&sort=1">' + "*" + "</a></th>";

    titleTableBody.append(rowHTML);
}
function handleLookup(query, doneCallBack) {
    if(query.length >= 3) {
        console.log("LOOKING UP " + query);
        $.ajax({
            method : "GET",
            url : "api/fulltext?query=" + encodeURIComponent(query),
            success : function(data) {
                handleSuccess(data, query, doneCallBack)
            },
            error : function(errorData) {
                console.log("lookup ajax error");
                console.log(errorData);
            }
        })
    }
}
function handleSuccess(data, query, doneCallBack) {
    let jsonData = JSON.parse(data);
    console.log(jsonData);

    doneCallBack({suggestions : jsonData});
}

function handleSelect(suggestion) {

}

$("#autocomplete").autocomplete({
    lookup: function(query, doneCallBack) {
        handleLookup(query, doneCallBack)
    },
    onSelect: function(suggestion) {
        let url = window.location.origin + window.location.pathname + "single-movie.html?id=" + suggestion.data.id;
        window.location.assign(url);
    },
    deferRequestBy: 500,
    appendTo: "#results"

});

$("#autocomplete").keypress(function(event) {
    switch (event.keyCode) {
        case 13:
            let fulltext = encodeURIComponent($(this).val());
            let url = window.location.origin + window.location.pathname + "movieList.html?fulltext=" + fulltext + "&pageSize=25&pageOffset=0&sort=1";
            console.log(url)
            window.location.assign(url);
    }
});


populateTitles();


jQuery.ajax({
    url: "api/index",
    method: "GET",
    dataType: "json",
    success: (resultData) => handleResult(resultData)
});



