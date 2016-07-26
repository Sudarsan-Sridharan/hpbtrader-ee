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
    listeners: {
        'cellclick': 'showTradeIbOrders'
    },
    columns: [{
        xtype: 'templatecolumn',
        text: 'ID',
        width: 100,
        dataIndex: 'id',
        tpl: '{strategyId}/{id}'
    }, {
        text: 'Init Open Date',
        width: 180,
        dataIndex: 'initOpenDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Type',
        width: 80,
        dataIndex: 'tradeType',
        renderer: function(val, metadata, record) {
            metadata.style = (val == 'LONG' ? 'color: blue;' : 'color: brown;');
            return val;
        }
    }, {
        text: 'Quantity',
        width: 80,
        dataIndex: 'quantity',
        align: 'right'
    }, {
        text: 'Position',
        width: 80,
        dataIndex: 'tradePosition',
        align: 'right'
    }, {
        text: 'Open',
        width: 100,
        dataIndex: 'openPrice',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'Init Stop',
        width: 100,
        dataIndex: 'initialStop',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'Stop',
        width: 100,
        dataIndex: 'stopLoss',
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
        text: 'Close',
        width: 100,
        dataIndex: 'closePrice',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'Close Date',
        width: 180,
        dataIndex: 'closeDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
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
        reference: 'tradesPaging',
        bind: '{trades}',
        dock: 'bottom',
        displayInfo: true
    }]
});