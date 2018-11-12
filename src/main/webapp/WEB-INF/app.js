$(function() {
    var token = null;
    var userId = null;
    var offset = 0;
    var count = 10;
    var total = 0;
    //alert("Please use this form to login");

    $("#signin").click(function (e) {
        e.preventDefault();
        jQuery.ajax ({
            url:  "/api/sessions",
            type: "POST",
            data: JSON.stringify({emailAddress:$("#inputEmail").val(), password: $("#inputPassword").val()}),
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        }).done(function(data){
            $("#greeting").text("Hello " + data.data.firstName);
            token = data.data.token;
            userId = data.data.userId;
        })
            .fail(function(data){
                $("#greeting").text("Please try again!");
            })
    });

})