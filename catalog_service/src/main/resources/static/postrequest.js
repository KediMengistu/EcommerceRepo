$(document).ready(function () {

    // SUBMIT FORM
    $("#searchform").submit(function (event) {
        // Prevent the form from submitting via the browser.
        event.preventDefault();
        ajaxPost();
    });

    function ajaxPost() {

        // PREPARE FORM DATA
        var formData = {
            sellerid:$("#sellerid").val(),
            itemname: $("#itemname").val(),
            itemdescription: $("#itemdescription").val(),
            auctiontype: $("#auctiontype").val(),
            startprice: $("#startprice").val(),
            shippingprice: $("#shippingprice").val(),
            expeditedcost: $("#expeditedcost").val(),
            duration: $("#duration").val(), // Assuming the input field for duration is of type text
            enddate: $("#enddate").val() // Assuming the input field for enddate is of type text
        }

        // DO POST
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "sell",
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function (result) {
                if (result.status == "success") {
                    $("#postResultDiv").html(
                        "" + result.data.itemname
                        + "Post Successfully! <br>"
                        + "---> Congrats !!" + "</p>");
                } else {
                    $("#postResultDiv").html("<strong>Error</strong>");
                }
                console.log(result);
            },
            error: function (e) {
                alert("Error!")
                console.log("ERROR: ", e);
            }
        });

    }

})