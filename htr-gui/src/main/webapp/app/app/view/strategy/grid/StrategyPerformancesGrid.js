/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.StrategyPerformancesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-strategyperformances-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'HtrGui.view.strategy.StrategyController'
    ],
    bind: '{strategyPerformance}',
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        xtype: 'templatecolumn',
        text: 'ID',
        width: 100,
        dataIndex: 'id',
        tpl: '{strategyId}/{id}'
    }, {
        text: 'Performance Date',
        width: 180,
        dataIndex: 'performanceDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'All-Fil',
        width: 80,
        dataIndex: 'numAllOrders',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numAllOrders'] + '-' + record.data['numFilledOrders'];
        }
    }, {
        text: 'Sho-Lon',
        width: 80,
        dataIndex: 'numShorts',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numShorts'] + '-' + record.data['numLongs'];
        }
    }, {
        text: 'Win-Los',
        width: 80,
        dataIndex: 'numWinners',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numWinners'] + '-' + record.data['numLosers'];
        }
    }, {
        text: 'PL',
        width: 80,
        dataIndex: 'cumulativePl',
        align: 'right'
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'strategyPerformancePaging',
        bind: '{strategyPerformance}',
        dock: 'bottom',
        displayInfo: true
    }]
});