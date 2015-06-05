$(document).ready(function () {
    restUrlPrefix = "http://" + getUrlBase()  + "/rest/atr";
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
    refreshChart();
    createCumulPlChart();
    createTradePlChart();
});

function getUrlBase() {
    return window.location.host + window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
}