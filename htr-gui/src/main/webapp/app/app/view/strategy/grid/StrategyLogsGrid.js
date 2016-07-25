/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.StrategyLogsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-strategylogs-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'HtrGui.view.strategy.StrategyController'
    ],
    bind: '{strategyLogs}',
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
        reference: 'strategyLogsPaging',
        bind: '{strategyLogs}',
        dock: 'bottom',
        displayInfo: true
    }]
});