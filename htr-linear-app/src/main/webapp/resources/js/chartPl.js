function createCumulPlChart() {
    $.getJSON(restUrlPrefix + '/strategylogs', function(plData) {
        if (plData == null) {
            return;
        }
        var cumulPl = [];

        for (i = 0; i < plData.length; i++) {
            cumulPl.push([
                plData[i].timeInMillis,
                plData[i].cumulativePl
            ]);
        }

        // Create the chart
        $('#cumulPlChart').highcharts('StockChart', {
            title: {
                text: 'Strategy Cumulative PL'
            },
            yAxis: {
                title: {
                    text: 'PL'
                }
            },
            series: [{
                    name: 'Cumul PL',
                    data: cumulPl
                }]
        });
    });
}

function createTradePlChart() {
    $.getJSON(restUrlPrefix + '/trades', function(plData) {
        if (plData == null) {
            return;
        }
        var tradePl = [];

        for (i = 0; i < plData.length; i++) {
            tradePl.push([
                plData[i].timeInMillis,
                plData[i].realizedPl
            ]);
        }

        // Create the chart
        $('#tradePlChart').highcharts('StockChart', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Trade PL'
            },
            yAxis: {
                title: {
                    text: 'PL'
                }
            },
            series: [{
                    name: 'Trade PL',
                    data: tradePl
                }]
        });
    });
}
    
function createTradeLogChart() {
    $.getJSON(restUrlPrefix + '/tradelogs', function(logData) {
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
        $('#tradeLogChart').highcharts('StockChart', {
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