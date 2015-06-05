$(document).ready(function() {
    restUrlPrefix = "http://" + getUrlBase() + "/rest/linear";
    wsUrlPrefix = "ws://" + getUrlBase()  + "/websocket/series";
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
    websocketInit();
});

function getUrlBase() {
    return window.location.host + window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
}

function websocketInit() {
    $("#wsOutput").html("wsinit");
    var ws = new WebSocket(wsUrlPrefix);
    ws.onopen = function(evt) {
        updateWebsocketMessage("wsopened", 'col-green');
    };
    ws.onclose = function(evt) {
        updateWebsocketMessage("wsclosed", 'col-red');
    };
    ws.onmessage = function(evt) {
        var msg = evt.data;
        if (msg.substring(0, 2) == "qu") {
            updateWebsocketMessage(msg, 'col-green');
            updateSeriesTable();
        } else if (msg.substring(0, 2) == "su") {
            updateWebsocketMessage(msg, 'col-green');
            updateSeriesTable();
            updateRecentOrderTable();
        } else if (msg.substring(0, 2) == "rt") {
            updateRealtimeData(msg);
        }
    };
    ws.onerror = function(evt) {
        updateWebsocketMessage("wserr", 'col-red');
    };
}

function updateWebsocketMessage(msg, className) {
    $("#wsOutput").removeClass('col-green').removeClass('col-red').addClass(className).html(msg);
}

function updateRealtimeData(msg) {
    var arr = msg.split(",");
    $("td." + arr[1]  + "." + arr[2]).removeClass('col-green').removeClass('col-orange').removeClass('col-yellow').removeClass('col-blue-bck'). removeClass('col-red-bck').html(arr[3]).addClass(arr[4]);
}

function handleManualOrder(xhr, status, args) {
    if (!args.validationFailed) {
        PF('manualOrderDlg').hide();
    }
}

function handleNewSeries(xhr, status, args) {
    if (!args.validationFailed) {
        PF('newSeriesDlg').hide();
        updateSeriesTable();
    }
}

function createLastTradeLogChart() {
    $.getJSON(restUrlPrefix + '/lasttradelogs', function(logData) {
        if (logData == null) {
            return;
        }
        var stop = [];
        var price = [];
        var target = [];

        for (i = 0; i < logData.length; i++) {
            stop.push([
                logData[i].timeInMillis,
                logData[i].stopLoss
            ]);
            price.push([
                logData[i].timeInMillis,
                logData[i].price
            ]);
            target.push([
                logData[i].timeInMillis,
                logData[i].profitTarget
            ]);
        }

        // Create the chart
        $('#lastTradeLogChart').highcharts('StockChart', {
            title: {
                text: 'Trade Log'
            },
            series: [{
                name: 'Stop',
                data: stop
            }, {
                name: 'Price',
                data: price
            }, {
                name: 'Target',
                data: target
            }]
        });
    });
}