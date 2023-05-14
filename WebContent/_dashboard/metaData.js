let metaData = $('#metaData');
function handleResult(data) {
    let rowHTML = "";
    for(let i = 0; i < data.length; ++i) {
        let tableName = data[i]["tableName"];
        let variables = data[i]["variables"];
        rowHTML += "<h3>" + tableName + "</h3>";
        rowHTML += "<table class='table border border-grooved'>";
        rowHTML += "<tr><th>Column Name</th><th>Data Type</th></tr>";
        for(let j = 0; j < variables.length; ++j) {
            let columnName = variables[j]["columnName"];
            let dataType = variables[j]["dataType"];
            rowHTML += "<tr>" + "<td>" + columnName + "</td>" + "<td>" + dataType + "</td></tr>";
        }
        rowHTML += "</table>";
    }
    metaData.append(rowHTML);
}

$.ajax({
    url: "api/metadata",
    method: "GET",
    dataType: "json",
    success: (resultData) => handleResult(resultData)
});

