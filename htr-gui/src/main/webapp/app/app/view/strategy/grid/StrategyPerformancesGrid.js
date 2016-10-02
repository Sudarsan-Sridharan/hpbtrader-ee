/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.StrategyPerformancesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-strategyperformances-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    bind: '{strategyPerformances}',
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
        width: 100,
        dataIndex: 'numAllOrders',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numAllOrders'] + '-' + record.data['numFilledOrders'];
        }
    }, {
        text: 'Sho-Lo',
        width: 100,
        dataIndex: 'numShorts',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numShorts'] + '-' + record.data['numLongs'];
        }
    }, {
        text: 'Win-Los',
        width: 100,
        dataIndex: 'numWinners',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numWinners'] + '-' + record.data['numLosers'];
        }
    }, {
        text: 'PL',
        width: 100,
        dataIndex: 'cumulativePl',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.style = val < 0 ? 'color: red;' : 'color: green;';
            return Ext.util.Format.number(val, '0.00');
        }
    }, {
        flex: 1
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'strategyPerformancesPaging',
        bind: '{strategyPerformances}',
        dock: 'bottom',
        displayInfo: true
    }]
});