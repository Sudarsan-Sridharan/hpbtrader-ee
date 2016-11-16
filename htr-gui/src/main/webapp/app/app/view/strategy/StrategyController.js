/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.StrategyController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'HtrGui.common.Definitions',
        'HtrGui.view.strategy.window.IbOrderEventsWindow',
        'HtrGui.view.strategy.grid.IbOrderEventsGrid',
        'HtrGui.view.strategy.window.TradeIbOrdersWindow',
        'HtrGui.view.strategy.grid.TradeIbOrdersGrid'
    ],

    alias: 'controller.htr-strategy',

    init: function() {
        var me = this,
            strategies = me.getStore('strategies'),
            strategiesGrid; // strange bug, strategiesGrid reference ready only after some time

        me.prepare(function() {
            if (strategies) {
                strategies.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixStrategy + '/strategies');
                strategies.load(function (records, operation, success) {
                    if (success) {
                        console.log('loaded strategies');
                        strategies = me.getStore('strategies');
                        strategiesGrid = me.lookupReference('strategiesGrid');
                        strategiesGrid.setSelection(strategies.first());
                    }
                });
            }
        });

        var ws = new WebSocket(HtrGui.common.Definitions.wsUrlStrategy);
        ws.onopen = function(evt) {
            console.log('WS strategy opened');
        };
        ws.onclose = function(evt) {
            console.log('WS strategy closed');
        };
        ws.onmessage = function(evt) {
            var msg = evt.data,
                arr = msg.split(","),
                strategyId;

            console.log('WS strategy message: ' + msg);
            if (arr[0] == 'strategyId') {
                me.reloadStore(me.getStore('strategies'), 'strategies');
                if (me.strategyId == arr[1]) {
                    me.reloadStore(me.getStore('strategyPerformances'), 'strategyPerformances');
                }
            } else if (arr[0] == 'ibOrder') {
                if (me.strategyId == arr[3]) {
                    me.reloadStore(me.getStore('ibOrders'), 'ibOrders');
                }
            } else if (arr[0] == 'trade') {
                if (me.strategyId == arr[3]) {
                    me.reloadStore(me.getStore('trades'), 'trades');
                }
            }
        };
        ws.onerror = function(evt) {
            console.log('WS strategy error');
        };
    },

    reloadStore: function(store, storeName) {
        var me = this;

        if (store.isLoaded()) {
            store.reload();
        } else {
            store.load(function (records, operation, success) {
                if (success) {
                    console.log('loaded ' + storeName + ' for strategyId=' + me.strategyId);
                }
            });
        }
    },

    prepare: function(callback) {
        var me = this,
            prefix = HtrGui.common.Definitions.urlPrefixStrategy;

        var ajaxQueue = function(step) {
            switch(step) {
                case 0: Ext.Ajax.request({
                    url: prefix + '/codemap/iborderstatus/texts',
                    success: function (response, opts) {
                        me.ibOrderStatusTexts = Ext.decode(response.responseText);
                        ajaxQueue(1);
                    }}); break;
                case 1: Ext.Ajax.request({
                    url: prefix + '/codemap/iborderstatus/colors',
                    success: function (response, opts) {
                        me.ibOrderStatusColors = Ext.decode(response.responseText);
                        ajaxQueue(2);
                    }}); break;
                case 2: Ext.Ajax.request({
                    url: prefix + '/codemap/strategymode/colors',
                    success: function (response, opts) {
                        me.strategyModeColors = Ext.decode(response.responseText);
                        ajaxQueue(3);
                    }}); break;
                case 3: Ext.Ajax.request({
                    url: prefix + '/codemap/tradetype/texts',
                    success: function (response, opts) {
                        me.tradeTypeTexts = Ext.decode(response.responseText);
                        ajaxQueue(4);
                    }}); break;
                case 4: Ext.Ajax.request({
                    url: prefix + '/codemap/tradetype/colors',
                    success: function (response, opts) {
                        me.tradeTypeColors = Ext.decode(response.responseText);
                        ajaxQueue(5);
                    }}); break;
                case 5: Ext.Ajax.request({
                    url: prefix + '/codemap/tradestatus/texts',
                    success: function (response, opts) {
                        me.tradeStatusTexts = Ext.decode(response.responseText);
                        ajaxQueue(6);
                    }}); break;
                case 6: Ext.Ajax.request({
                    url: prefix + '/codemap/tradestatus/colors',
                    success: function (response, opts) {
                        me.tradeStatusColors = Ext.decode(response.responseText);
                        callback();
                    }}); break;
            }
        };
        ajaxQueue(0);
    },

    ibOrderStatusRenderer: function(val, metadata, record) {
        var me = this;
        metadata.style = 'cursor: pointer; background-color: ' + me.ibOrderStatusColors[val] + '; color: white;';
        return me.ibOrderStatusTexts[val];
    },

    ibOrderStatusRendererEvents: function(val, metadata, record) {
        var me = this;
        metadata.style = 'background-color: ' + me.ibOrderStatusColors[val] + '; color: white;';
        return me.ibOrderStatusTexts[val];
    },

    strategyModeRendererStrategy: function(val, metadata, record) {
        var me = this;
        metadata.style = record.data['active'] == true ? 'color: ' + me.strategyModeColors[val] : 'color: LightGray';
        return val;
    },

    strategyModeRenderer: function(val, metadata, record) {
        var me = this;
        metadata.style = 'color: ' + me.strategyModeColors[val];
        return val;
    },

    tradeTypeRenderer: function(val, metadata, record) {
        var me = this;
        metadata.style = 'color: ' + me.tradeTypeColors[val];
        return me.tradeTypeTexts[val];
    },

    tradeStatusRenderer: function(val, metadata, record) {
        var me = this;
        metadata.style = 'cursor: pointer; color: white; background-color: ' + me.tradeStatusColors[val];
        return me.tradeStatusTexts[val];
    },

    tradeStatusRendererLog: function(val, metadata, record) {
        var me = this;
        metadata.style = 'color: ' + me.tradeStatusColors[val];
        return me.tradeStatusTexts[val];
    },

    onStrategySelect: function(grid, record, index, eOpts) {
        var me = this,
            strategyPerformances = me.getStore('strategyPerformances'),
            performanceChartDataPoints = me.getStore('performanceChartDataPoints'),
            strategyPerformancesPaging = me.lookupReference('strategyPerformancesPaging'),
            ibOrders = me.getStore('ibOrders'),
            ibOrdersPaging = me.lookupReference('ibOrdersPaging'),
            trades = me.getStore('trades'),
            tradesPaging = me.lookupReference('tradesPaging');

        me.strategyId = record.data.id;
        strategyPerformances.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixStrategy + '/strategies/' + me.strategyId + '/strategyperformances/trading');
        performanceChartDataPoints.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixStrategy + '/strategies/' + me.strategyId + '/strategyperformances/trading/chart');
        ibOrders.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixStrategy + '/strategies/' + me.strategyId + '/iborders/trading');
        trades.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixStrategy + '/strategies/' + me.strategyId + '/trades/trading');

        strategyPerformances.load(function (records, operation, success) {
            if (success) {
                console.log('loaded strategyPerformances for strategyId=' + me.strategyId);
            }
        });
        performanceChartDataPoints.load(function (records, operation, success) {
            if (success) {
                console.log('loaded performanceChartDataPoints for strategyId=' + me.strategyId);
                me.createPerformanceChart();
            }
        });
        ibOrders.load(function (records, operation, success) {
            if (success) {
                console.log('loaded ibOrders for strategyId=' + me.strategyId);
            }
        });
        trades.load(function (records, operation, success) {
            if (success) {
                console.log('loaded trades for strategyId=' + me.strategyId);
                me.lookupReference('tradesGrid').setSelection(trades.first());
            }
        });
    },

    createPerformanceChart: function(tabPanel, newCard, oldCard, eOpts) {
        var me = this,
            performanceChartDataPoints = me.getStore('performanceChartDataPoints'),
            cumulativePl = [];

        console.log('Creating performance chart');
        if (!Ext.get('hpb_cumulative_pl_chart')) {
            return;
        }
        cumulativePl.push(['Date', 'Cumulative PL']);
        var cumPl = 0;
        performanceChartDataPoints.each(function (record, id) {
            var rd = record.data;
            if (cumPl != rd.cumulativePl) { // filter out adjacent data points with the same pl
                cumulativePl.push([new Date(rd.performanceDate), rd.cumulativePl]);
                cumPl = rd.cumulativePl;
            }
        });
        GoogleChart.ceateLineChart(cumulativePl, 'Cumulative PL', 'hpb_cumulative_pl_chart');
    },

    deleteStrategy: function(button, evt) {
        var me = this,
            strategies = me.getStore('strategies'),
            strategiesGrid = me.lookupReference('strategiesGrid'),
            strategyId = button.getWidgetRecord().data.id,
            ibOrders = me.getStore('ibOrders'),
            trades = me.getStore('trades'),
            tradeLogs = me.getStore('tradeLogs');

        Ext.Msg.show({
            title: 'Delete strategy, id=' + strategyId + '?',
            message: 'All associated data will be deleted',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        method: 'DELETE',
                        url: HtrGui.common.Definitions.urlPrefixStrategy + '/strategies/' + strategyId,
                        success: function(response, opts) {
                            strategies.load(function(records, operation, success) {
                                if (success) {
                                    if (strategies.getCount() == 0) {
                                        ibOrders.removeAll();
                                        trades.removeAll();
                                        tradeLogs.removeAll();
                                    } else {
                                        strategiesGrid.setSelection(strategies.first());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    },

    showIbOrderEvents: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex != 2) {
            return;
        }
        var me = this;

        if (!me.ibOrderEventsGrid) {
            me.ibOrderEventsGrid =  Ext.create('HtrGui.view.strategy.grid.IbOrderEventsGrid');
            me.ibOrderEventsWindow = Ext.create('widget.htr-strategy-iborderevents-window');
            me.ibOrderEventsWindow.add(me.ibOrderEventsGrid);
            me.getView().add(me.ibOrderEventsWindow);
        }
        me.ibOrderEventsGrid.setStore(record.ibOrderEvents());
        me.ibOrderEventsWindow.show();
    },

    showTradeIbOrders: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex != 2) {
            return;
        }
        var me = this;

        if (!me.tradeIbOrdersGrid) {
            me.tradeIbOrdersGrid =  Ext.create('HtrGui.view.strategy.grid.TradeIbOrdersGrid');
            me.tradeIbOrdersWindow = Ext.create('widget.htr-strategy-tradeiborders-window');
            me.tradeIbOrdersWindow.add(me.tradeIbOrdersGrid);
            me.getView().add(me.tradeIbOrdersWindow);
        }
        var tradeId = record.get(record.getFields()[0].getName());
        me.tradeIbOrdersGrid.setStore(record.tradeIbOrders());
        me.tradeIbOrdersWindow.show();
    },

    onTradeSelect: function(grid, record, index, eOpts) {
        var me = this,
            tradeLogs = me.getStore('tradeLogs'),
            tradeLogsPaging = me.lookupReference('tradeLogsPaging');

        me.tradeId = record.data.id;
        tradeLogs.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixStrategy + '/strategies/trade/' + me.tradeId + '/tradelogs/trading');

        tradeLogs.load(function (records, operation, success) {
            if (success) {
                console.log('loaded tradeLogs for tradeId=' + me.tradeId);
            }
        });
    },

    setGlyphs: function() {
        var me = this,
            performancePanel = me.lookupReference('performancePanel'),
            ibOrdersGrid = me.lookupReference('ibOrdersGridStrategy'),
            tradesPanel = me.lookupReference('tradesPanel');

            performancePanel.setGlyph(HtrGui.common.Glyphs.getGlyph('fa_sort_amount_asc'));
            ibOrdersGrid.setGlyph(HtrGui.common.Glyphs.getGlyph('fa_list_ol'));
            tradesPanel.setGlyph(HtrGui.common.Glyphs.getGlyph('fa_money'));
    }
});
