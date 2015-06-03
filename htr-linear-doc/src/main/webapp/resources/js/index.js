$(document).ready(function() {
    $("body").layout({
        applyDefaultStyles: true,
        closable: false,
        north__size: 45,
        north__resizable: false,
        west__size: 300,
        west__minSize: 200,
        west__maxSize: 400
    });
    $("#overview").menu();
    $("#overviewTest").menu();
    $("#accordion" ).accordion({
        heightStyle: "content"
    });
    $("#content").load("introduction/architecture.xhtml");
});