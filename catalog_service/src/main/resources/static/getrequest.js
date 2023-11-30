GET: $(document).ready(
    function ajaxGet() {
        $.ajax({
            type: "GET",
            url: "entirecatalog",
            success: function (result) {
                if (result.status == "success") {
                    $('#getResultDiv ul').empty();
                    $.each(result.data, function (i, catalog) {
                        var itemInfo = "Item ID: " + catalog.itemid +
                            ", Seller ID: " + catalog.sellerid +
                            ", Item Name: " + catalog.itemname +
                            ", Item Description: " + catalog.itemdescription +
                            ", Auction Type: " + catalog.auctiontype +
                            ", Start Price: " + catalog.startprice +
                            ", Shipping Price: " + catalog.shippingprice +
                            ", Expedited Cost: " + catalog.expeditedcost +
                            ", Duration: " + catalog.duration +
                            ", End Date: " + catalog.enddate + "<br>";

                        $('#getResultDiv .list-group').append(itemInfo);
                    });
                    console.log("Success: ", result);
                } else {
                    $("#getResultDiv").html("<strong>Error</strong>");
                    console.log("Fail: ", result);
                }
            },
            error: function (e) {
                $("#getResultDiv").html("<strong>Error</strong>");
                console.log("ERROR: ", e);
            }
        });
    })
