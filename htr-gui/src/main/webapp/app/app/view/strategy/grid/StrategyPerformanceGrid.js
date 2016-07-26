/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.StrategyPerformanceGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-strategylogs-grid',
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
        text: 'ID',
        width: 80,
        dataIndex: 'id',
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