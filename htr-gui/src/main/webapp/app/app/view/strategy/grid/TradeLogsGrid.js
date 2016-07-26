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
        xtype: 'templatecolumn',
        text: 'ID',
        width: 100,
        dataIndex: 'id',
        tpl: '{tradeId}/{id}'
    }, {
        text: 'Log Date',
        width: 180,
        dataIndex: 'logDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Position',
        width: 80,
        dataIndex: 'tradePosition',
        align: 'right'
    }, {
        text: 'Stop',
        width: 100,
        dataIndex: 'stopLoss',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'Price',
        width: 100,
        dataIndex: 'price',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'target',
        width: 100,
        dataIndex: 'profitTarget',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'Unreal P/L',
        width: 100,
        dataIndex: 'unrealizedPl',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.style = val < 0 ? 'color: red;' : 'color: green;';
            return Ext.util.Format.number(val, '0.00');
        }
    }, {
        text: 'Real P/L',
        width: 100,
        dataIndex: 'realizedPl',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.style = val < 0 ? 'color: red;' : 'color: green;';
            return Ext.util.Format.number(val, '0.00');
        }
    }, {
        flex: 1
    }, {
        text: 'Status',
        width: 60,
        dataIndex: 'tradeStatus',
        renderer: function(val, metadata, record) {
            metadata.style = 'cursor: pointer; color: white; ' + (val == 'OPEN' ? 'background-color: green;' : 'background-color: brown;');
            return val.toLowerCase();
        }
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'tradeLogsPaging',
        bind: '{tradeLogs}',
        dock: 'bottom',
        displayInfo: true
    }]
});