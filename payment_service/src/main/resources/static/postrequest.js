 $(document).ready(function () {
    $("#paymentform").submit(function (event) {
      event.preventDefault();
      ajaxPost();
    });

    function ajaxPost() {
      var formData = {
        username:$("#username").val(),
        paidauctionid: $("#paidauctionid").val()
        cardnum:$("#cardnum").val(),
        cardfname: $("#cardfname").val(),
        cardlname: $("#cardlname").val(),
        expdate: $("#expdate").val(),
        securitycode: $("#securitycode").val()
        }

      $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "http://localhost:8093/ecommerce/payment/insertpaymentinfo",
        data: JSON.stringify(formData),
        dataType: 'json',
        success: function (result) {
          if (result.status == "success") {
            $("#postResultDiv").html(
                    "" + "result.data.cardFirstName" + "Post Successfully! <br>" + "---> Congrats !!" + "</p>"
            );
          } else {
            $("#postResultDiv").html("<strong>Error</strong>");
          }
          console.log(result);
        },

      });
    })