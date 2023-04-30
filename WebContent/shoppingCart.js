let cartTableBody = $('#cart_table_body');
let emptyCart = $('#empty_cart');
function handleResult(resultData) {
    console.log("Success");
    if(resultData.length === 0) {emptyCart.text("EMPTY CART"); return;}
    let rowHTML = "";
    for(let i = 0; i < resultData.length; i++) {
        rowHTML += "<tr>";
        rowHTML += "<th>" + "<a href='single-movie.html?id=" + resultData[i]["id"] + "'>" +
                    resultData[i]["title"] + "</a></th>";
        rowHTML += "<th>$8</th>"
        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";
        rowHTML += "<th>" + '<button type="button" class="btn btn-outline-success addButton" data-value="' + resultData[i]["id"] + '">' +
            "Add</button>";
        rowHTML += '<button type="button" class="btn btn-outline-danger removeButton ml-2"  data-value="' + resultData[i]["id"] + '">' +
            "Remove</button></th>";
        let quantity = parseInt(resultData[i]["quantity"], 10);
        let totalPrice = quantity * 8;
        rowHTML += "<th>$" + totalPrice.toString() + "</th>";
        rowHTML += '<th><button type="button" class="btn btn-outline-danger deleteButton ml-2"  data-value="' + resultData[i]["id"] + '">' +
            "Delete</button></th>";
        rowHTML += "</tr>";

    }
    cartTableBody.append(rowHTML);
}

function addToCart(event) {
    event.preventDefault();
    let data = $(this).data("value");
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/CartAdd?movieId=" + data + "&action=0", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: function (resultData) {
            alert("Added to cart");
        }
    });
    window.location.reload();
}

function removeFromCart(event) {
    event.preventDefault();
    let data = $(this).data("value");
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/CartAdd?movieId=" + data + "&action=1", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: function (resultData) {
            alert("Added to cart");
        }
    });
    window.location.reload();
}
function deleteFromCart(event) {
    event.preventDefault();
    let data = $(this).data("value");
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/CartAdd?movieId=" + data + "&action=2", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: function (resultData) {
            alert("deleted");
        }
    });
    window.location.reload();
}

$(document).on('click', '.addButton', addToCart);
$(document).on('click', '.removeButton', removeFromCart);
$(document).on('click', '.deleteButton', deleteFromCart);

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shoppingCart",
    success: (resultData) => handleResult(resultData)
});
