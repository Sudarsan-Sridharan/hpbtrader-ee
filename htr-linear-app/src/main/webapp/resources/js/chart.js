var chart;

function refreshChart() {
    if (chart != null) {
        chart.destroy();
    }
    createChart("/chartparams");
}

function createChart(restUrlSuffix) {
    $.getJSON(restUrlPrefix + restUrlSuffix, function(data) {
        if (data == null) {
            return;
        }
        var chartParams = data;
        var quotesUrl = restUrlPrefix + "/quotes/" + chartParams.seriesId + "?numBars=" + chartParams.numBars;
        var ema1Url = restUrlPrefix + "/ema/" + chartParams.seriesId + "/" + chartParams.ema1Period + "?numBars=" + chartParams.numBars;
        var ema2Url = restUrlPrefix + "/ema/" + chartParams.seriesId + "/" + chartParams.ema2Period + "?numBars=" + chartParams.numBars;
        var stochUrl = restUrlPrefix + "/stoch/" + chartParams.seriesId  + "?numBars=" + chartParams.numBars;
        var macdUrl = restUrlPrefix + "/macd/" + chartParams.seriesId  + "?numBars=" + chartParams.numBars;
        
        var ohlc = [];
        var volume = [];
        var ema1 = [];
        var ema2 = [];
        var stochK = [];
        var stochD = [];
        var macdL = [];
        var macdSl = [];
        var macdH = [];
        var jsonCounter = 0;
    
        $.getJSON(quotesUrl, function(data) {
            if (data == null) {
                return;
            }
            for (i = 0; i < data.length; i++) {
                ohlc.push([
                    parseInt(data[i].timeInMillisBarClose),
                    parseFloat(data[i].qOpen),
                    parseFloat(data[i].high),
                    parseFloat(data[i].low),
                    parseFloat(data[i].qClose)
                    ]);

                volume.push([
                    parseInt(data[i].timeInMillisBarClose),
                    parseInt(data[i].volume)
                    ]);
            }
            if (++jsonCounter == 5) {
                doCreateChart(chartParams, ohlc, volume, ema1, ema2, stochK, stochD, macdL, macdSl, macdH);
            }
        });
        $.getJSON(ema1Url, function(data) {
            if (data == null) {
                return;
            }
            for (i = 0; i < data.length; i++) {
                ema1.push([
                    parseInt(data[i].timeInMillis),
                    parseFloat(data[i].ema)
                    ]);
            }
            if (++jsonCounter == 5) {
                doCreateChart(chartParams, ohlc, volume, ema1, ema2, stochK, stochD, macdL, macdSl, macdH);
            }
        });
        $.getJSON(ema2Url, function(data) {
            if (data == null) {
                return;
            }
            for (i = 0; i < data.length; i++) {
                ema2.push([
                    parseInt(data[i].timeInMillis),
                    parseFloat(data[i].ema)
                    ]);
            }
            if (++jsonCounter == 5) {
                doCreateChart(chartParams, ohlc, volume, ema1, ema2, stochK, stochD, macdL, macdSl, macdH);
            }
        });
        $.getJSON(stochUrl, function(data) {
            if (data == null) {
                return;
            }
            for (i = 0; i < data.length; i++) {
                stochK.push([
                    parseInt(data[i].timeInMillis),
                    parseFloat(data[i].stochK)
                    ]);
                stochD.push([
                    parseInt(data[i].timeInMillis),
                    parseFloat(data[i].stochD)
                    ]);
            }
            if (++jsonCounter == 5) {
                doCreateChart(chartParams, ohlc, volume, ema1, ema2, stochK, stochD, macdL, macdSl, macdH);
            }
        });
        $.getJSON(macdUrl, function(data) {
            if (data == null) {
                return;
            }
            for (i = 0; i < data.length; i++) {
                macdL.push([
                    parseInt(data[i].timeInMillis),
                    parseFloat(data[i].macdL)
                    ]);
                 macdSl.push([
                    parseInt(data[i].timeInMillis),
                    parseFloat(data[i].macdSl)
                    ]);
                 macdH.push([
                    parseInt(data[i].timeInMillis),
                    parseFloat(data[i].macdH)
                    ]);
            }
            if (++jsonCounter == 5) {
                doCreateChart(chartParams, ohlc, volume, ema1, ema2, stochK, stochD, macdL, macdSl, macdH);
            }
        });
    });
}

function doCreateChart(chartParams, ohlc, volume, ema1, ema2, stochK, stochD, macdL, macdSl, macdH) {
    chart = new Highcharts.StockChart({
        chart : {
            renderTo : 'chart',
            alignTicks: false
        },
        plotOptions : {
            line : {
                lineWidth: 1
            }
        },
        tooltip: {
            valueDecimals: 6
        },
        rangeSelector : {
            buttons : [{
                type : 'hour',
                count : 2,
                text : '2H'
            }, {
                type : 'day',
                count : 1,
                text : '1D'
            }, {
                type : 'day',
                count : 5,
                text : '5D'
            }, {
                type : 'week',
                count : 2,
                text : '2W'
            }, {
                type : 'month',
                count : 1,
                text : '1M'
            }, {
                type : 'month',
                count : 3,
                text : '3M'
            }, {
                type : 'all',
                count : 1,
                text : 'All'
            }],
            selected : 4,
            inputEnabled : false
        },

        title : {
            text : "seriesId=" + chartParams.seriesId + ", " + chartParams.symbol + ", " + chartParams.currency + ", " + chartParams.interval + ", bars=" + chartParams.numBars
        },

        yAxis: [{
            title: {
                text: 'Price'
            },
            height: 400
        }, {
            title: {
                text: 'Volume'
            },
            top: 265,
            height: 200,
            offset: 50,
            gridLineColor: 'transparent'
        }, {
            title: {
                text: 'Stoch'
            },
            top: 490,
            height: 200,
            offset: 0,
            min: 0,
            max: 100,
            tickInterval: 20
        }, {   
            title: {
                text: 'MACD'
            },
            top: 710,
            height: 200,
            offset:0
        }],

        series : [{
            type : 'candlestick',
            name : 'Stock Price',
            data : ohlc,
            dataGrouping: {
                enabled: false
            },
            tooltip: {
                valueDecimals: 5
            }
        }, 
        {
            type: 'column',
            name: 'Volume',
            data: volume,
            yAxis: 1,
            dataGrouping: {
                enabled: false
            },
            tooltip: {
                valueDecimals: 0
            }
        },
        {
            name : 'EMA ' + chartParams.ema1Period,
            data : ema1,
            dataGrouping: {
                enabled: false
            },
            color: 'red'
        },
        {
            name : 'EMA ' + chartParams.ema2Period,
            data : ema2,
            dataGrouping: {
                enabled: false
            },
            color: 'green'
        },
        {
            name: 'Stoch K',
            data: stochK,
            yAxis: 2,
            dataGrouping: {
                enabled: false
            },
            color: 'black'
        },
        {
            name: 'Stoch D',
            data: stochD,
            yAxis: 2,
            dataGrouping: {
                enabled: false
            },
            color: 'red'
        },
        {
            name: 'MACD L',
            data: macdL,
            yAxis: 3,
            dataGrouping: {
                enabled: false
            },
            color: 'black'
        },
        {
            name: 'MACD SL',
            data: macdSl,
            yAxis: 3,
            dataGrouping: {
                enabled: false
            },
            color: 'red'
        },
        {
            type: 'column',
            name: 'MACD H',
            data: macdH,
            yAxis: 3,
            dataGrouping: {
                enabled: false
            },
            color: 'blue'
            
        }
        ]
    });
}