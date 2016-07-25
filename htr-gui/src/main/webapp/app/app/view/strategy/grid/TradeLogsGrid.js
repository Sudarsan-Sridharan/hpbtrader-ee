/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.TradeLogsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-tradelogs-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'HtrGui.view.strategy.StrategyController'
    ],
    bind: '{tradeLogs}',
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        text: 'ID',
        width: 80,
        dataIndex: 'id',
        align: 'right'
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'tradeLogsPaging',
        bind: '{tradeLogs}',
        dock: 'bottom',
        displayInfo: true
    }]
});