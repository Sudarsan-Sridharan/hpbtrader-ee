/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.TradesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-trades-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'HtrGui.view.strategy.StrategyController'
    ],
    bind: '{trades}',
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
        reference: 'tradesPaging',
        bind: '{trades}',
        dock: 'bottom',
        displayInfo: true
    }]
});