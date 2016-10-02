/**
 * Created by robertk on 25.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.IbOrdersGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-iborders-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    bind: '{ibOrders}',
    listeners: {
        cellclick: 'showIbOrderEvents'
    },
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
        text: 'Created Date',
        width: 180,
        dataIndex: 'createdDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Status',
        width: 90,
        dataIndex: 'status',
        renderer: 'ibOrderStatusRenderer'
    }, {
        text: 'IB Account',
        width: 100,
        dataIndex: 'ibAccountId'
    }, {
        text: 'Mode',
        width: 80,
        dataIndex: 'strategyMode',
        renderer: 'strategyModeRenderer'
    }, {
        text: 'PermId',
        width: 80,
        dataIndex: 'ibPermId',
        align: 'right',
        renderer: function(val, metadata, record) {
            return (val ? val : '-');
        }
    }, {
        text: 'Instrument',
        width: 250,
        dataIndex: 'symbol',
        renderer: function(val, metadata, record) {
            return (record.data['symbol'] + '-' + record.data['underlying'] + '-' + record.data['currency'] + '-' + record.data['secType'] + '-' + record.data['exchange']).toLowerCase();
        }
    }, {
        text: 'Action',
        width: 60,
        dataIndex: 'orderAction'
    }, {
        text: 'Qnt',
        width: 80,
        dataIndex: 'quantity',
        align: 'right'
    }, {
        text: 'Ord',
        width: 60,
        dataIndex: 'orderType'
    }, {
        text: 'Price',
        width: 80,
        dataIndex: 'orderPrice',
        align: 'right',
        renderer: function(val, metadata, record) {
            return (val ? Ext.util.Format.number(val, '0.00###') : '-');
        }
    }, {
        text: 'Fill',
        width: 80,
        dataIndex: 'fillPrice',
        align: 'right',
        renderer: function(val, metadata, record) {
            return (val ? Ext.util.Format.number(val, '0.00###') : '-');
        }
    }, {
        text: 'Ord',
        width: 60,
        dataIndex: 'ibOrderId',
        align: 'right',
        renderer: function(val, metadata, record) {
            return (val ? val : '-');
        }
    }, {
        text: 'HB',
        width: 60,
        dataIndex: 'heartbeatCount',
        align: 'right',
        renderer: function(val, metadata, record) {
            return (val ? val : '-');
        }
    }, {
        text: 'Submit',
        width: 80,
        dataIndex: 'submitType',
        align: 'right',
        renderer: function(val, metadata, record) {
            return val.toLowerCase();
        }
    }, {
        text: 'Trigger',
        flex: 1,
        dataIndex: 'triggerDesc',
        renderer: function(val, metadata, record) {
            metadata.tdAttr = 'data-qtip="' + val + '"';
            return val;
        }
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'ibOrdersPaging',
        bind: '{ibOrders}',
        dock: 'bottom',
        displayInfo: true
    }]
});