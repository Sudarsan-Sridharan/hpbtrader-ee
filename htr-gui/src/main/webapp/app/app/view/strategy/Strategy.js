/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.Strategy', {
    extend: 'Ext.panel.Panel',
    xtype: 'htr-strategy',

    requires: [
        'Ext.layout.container.VBox',
        'HtrGui.view.strategy.StrategyController',
        'HtrGui.view.strategy.StrategyModel',
        'HtrGui.view.strategy.grid.IbOrdersGrid',
        'HtrGui.view.strategy.grid.StrategiesGrid',
        'HtrGui.view.strategy.grid.StrategyPerformancesGrid',
        'HtrGui.view.strategy.grid.TradesGrid',
        'HtrGui.view.strategy.grid.TradeLogsGrid',
        'HtrGui.common.Glyphs',
        'Ext.tab.Panel'
    ],
    header: false,
    border: false,
    scrollable: true,
    controller: 'htr-strategy',
    viewModel: {
        type: 'htr-strategy'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [{
        xtype: 'htr-strategy-strategies-grid',
        title: 'Strategies',
        reference: 'strategiesGrid'
    }, {
        xtype: 'tabpanel',
        title: 'Details',
        listeners: {
            beforerender: 'setGlyphs'
        },
        items: [{
            xtype: 'panel',
            title: 'Performance',
            reference: 'performancePanel',
            items: [{
                xtype: 'htr-strategy-strategyperformances-grid',
                reference: 'strategyPerformancesGrid'
            }, {
                xtype: 'container',
                reference: 'chartsContainer',
                listeners: {
                    afterrender: 'createPerformanceChart'
                },
                items: [{
                    html: '<div id="hpb_cumulative_pl_chart" style="height: 600px; width: 1500px;"></div>'
                }]
            }]
        }, {
            xtype: 'htr-strategy-iborders-grid',
            title: 'IB Orders',
            reference: 'ibOrdersGridStrategy'
        }, {
            xtype: 'panel',
            title: 'Trades',
            reference: 'tradesPanel',
            items: [{
                xtype: 'htr-strategy-trades-grid',
                title: 'Trades',
                reference: 'tradesGrid'
            }, {
                xtype: 'htr-strategy-tradelogs-grid',
                title: 'Trade Logs',
                reference: 'tradeLogsGrid'
            }]
        }]
    }]
});