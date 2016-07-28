/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.StrategyController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'HtrGui.common.Definitions'
    ],

    alias: 'controller.htr-strategy',

    init: function() {
        var me = this,
            strategies = me.getStore('strategies'),
            strategiesGrid = me.lookupReference('strategiesGrid');

        me.prepare(function() {
            if (strategies) {
                strategies.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixExec + '/strategies');
                strategies.load(function (records, operation, success) {
                    if (success) {
                        console.log('loaded ibAccounts');
                        strategiesGrid.setSelection(strategies.first());
                    }
                });
            }
        });
    },

    prepare: function(callback) {
        var me = this,
            prefix = HtrGui.common.Definitions.urlPrefixExec;

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

    strategyRenderer: function(val, metadata, record) {
        var me = this;

        metadata.style = 'color: ' + me.strategyModeColors[val];
        return record.data['strategyId'] + '/' + val;
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

    onStrategySelect: function(grid, record, index, eOpts) {
        var me = this,
            strategyPerformances = me.getStore('strategyPerformances'),
            strategyPerformancesPaging = me.lookupReference('strategyPerformancesPaging'),
            ibOrders = me.getStore('ibOrders'),
            ibOrdersPaging = me.lookupReference('ibOrdersPaging'),
            trades = me.getStore('trades'),
            tradesPaging = me.lookupReference('tradesPaging'),
            tradesGrid = me.lookupReference('tradesGrid'),
            prefix = HtrGui.common.Definitions.urlPrefixStrategy;

        me.strategyId = record.data.id;
        strategyPerformances.getProxy().setUrl(prefix + '/' + me.strategyId + '/strategyperformances/trading');
        ibOrders.getProxy().setUrl(prefix + '/' + me.strategyId + '/iborders/trading');
        trades.getProxy().setUrl(prefix + '/' + me.strategyId + '/trades/trading');

        if (strategyPerformances.isLoaded()) {
            strategyPerformancesPaging.moveFirst();
        } else {
            strategyPerformances.load(function (records, operation, success) {
                if (success) {
                    console.log('loaded strategyPerformances for strategyId=' + me.strategyId);
                }
            });
        }
        if (ibOrders.isLoaded()) {
            ibOrdersPaging.moveFirst();
        } else {
            ibOrders.load(function (records, operation, success) {
                if (success) {
                    console.log('loaded ibOrders for for strategyId=' + me.strategyId);
                }
            });
        }
        if (trades.isLoaded()) {
            tradesPaging.moveFirst();
        } else {
            trades.load(function (records, operation, success) {
                if (success) {
                    console.log('loaded trades for for strategyId=' + me.strategyId);
                    tradesGrid.setSelection(trades.first());
                }
            });
        }
    },

    showIbOrderEvents: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex != 2) {
            return;
        }
        var me = this;

        if (!me.ibOrderEventsGrid) {
            me.ibOrderEventsGrid =  Ext.create('HtrGui.view.exec.grid.IbOrderEventsGrid');
            me.ibOrderEventsWindow = Ext.create('widget.htr-strategy-iborderevents-window');
            me.ibOrderEventsWindow.add(me.ibOrderEventsGrid);
        }
        var permId = record.get(record.getFields()[1].getName());
        me.ibOrderEventsGrid.setStore(record.ibOrderEvents());
        me.ibOrderEventsWindow.setTitle("IB Order Events, permId=" + permId);
        me.ibOrderEventsWindow.show();
    },

    showTradeIbOrders: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex != 2) {
            return;
        }
        var me = this;

        if (!me.tradeIbOrdersGrid) {
            me.tradeIbOrdersGrid =  Ext.create('HtrGui.view.exec.grid.TradeIbOrdersGrid');
            me.tradeIbOrdersWindow = Ext.create('widget.htr-strategy-tradeiborders-window');
            me.tradeIbOrdersWindow.add(me.tradeIbOrdersGrid);
        }
        var tradeId = record.get(record.getFields()[0].getName());
        me.tradeIbOrdersGrid.setStore(record.tradeIbOrders());
        me.tradeIbOrdersWindow.setTitle("Trade IB Orders, tradeId=" + tradeId);
        me.tradeIbOrdersWindow.show();
    },

    onTradeSelect: function(grid, record, index, eOpts) {
        var me = this,
            tradeLogs = me.getStore('tradeLogs'),
            tradeLogsPaging = me.lookupReference('tradeLogsPaging');

        me.tradeId = record.data.id;
        tradeLogs.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixStrategy + '/' + me.tradeId + '/tradelogs/trading');

        if (tradeLogs.isLoaded()) {
            tradeLogsPaging.moveFirst();
        } else {
            tradeLogs.load(function (records, operation, success) {
                if (success) {
                    console.log('loaded tradeLogs for tradeId=' + me.tradeId);
                }
            });
        }
    },

    setGlyphs: function() {
        var me = this;

        me.lookupReference('strategyLogsPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('fa_sort_amount_asc'));
        me.lookupReference('ibOrdersPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('fa_list_ol'));
        me.lookupReference('tradesPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('fa_money'));
    }
});
