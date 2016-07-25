/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.StrategiesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-strategies-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'HtrGui.view.strategy.StrategyController'
    ],
    bind: '{strategies}',
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
        reference: 'strategiesPaging',
        bind: '{strategies}',
        dock: 'bottom',
        displayInfo: true
    }]
});