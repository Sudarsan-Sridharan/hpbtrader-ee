/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.TradesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-trades-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    listeners: {
        select: 'onTradeSelect',
        cellclick: 'showTradeIbOrders'
    },
    bind: '{trades}',
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        xtype: 'templatecolumn',
        text: 'ID',
        width: 120,
        dataIndex: 'id',
        tpl: '{strategyId}/{id}'
    }, {
        text: 'Init Open Date',
        width: 180,
        dataIndex: 'initOpenDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Status',
        width: 90,
        dataIndex: 'tradeStatus',
        renderer: 'tradeStatusRenderer'
    }, {
        text: 'Type',
        width: 90,
        dataIndex: 'tradeType',
        renderer: 'tradeTypeRenderer'
    }, {
        text: 'Quantity',
        width: 90,
        dataIndex: 'quantity',
        align: 'right'
    }, {
        text: 'Position',
        width: 90,
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
            return val ? Ext.util.Format.number(val, '0.00###') : '-';
        }
    }, {
        text: 'Stop',
        width: 100,
        dataIndex: 'stopLoss',
        align: 'right',
        renderer: function(val, metadata, record) {
            return val ? Ext.util.Format.number(val, '0.00###') : '-';
        }
    }, {
        text: 'target',
        width: 100,
        dataIndex: 'profitTarget',
        align: 'right',
        renderer: function(val, metadata, record) {
            return val ? Ext.util.Format.number(val, '0.00###') : '-';
        }
    }, {
        text: 'Close',
        width: 100,
        dataIndex: 'closePrice',
        align: 'right'
    }, {
        text: 'Close Date',
        width: 180,
        dataIndex: 'closeDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Unrl P/L',
        width: 100,
        dataIndex: 'unrealizedPl',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.style = val < 0 ? 'color: red;' : 'color: green;';
            return Ext.util.Format.number(val, '0.00');
        }
    }, {
        text: 'P/L',
        width: 100,
        dataIndex: 'realizedPl',
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
        reference: 'tradesPaging',
        bind: '{trades}',
        dock: 'bottom',
        displayInfo: true
    }]
});