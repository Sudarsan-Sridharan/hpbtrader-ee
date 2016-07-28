/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.exec.grid.IbOrdersGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-exec-iborders-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'HtrGui.view.exec.ExecController'
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
        width: 80,
        dataIndex: 'status',
        renderer: 'ibOrderStatusRenderer'
    }, {
        text: 'IB Account',
        width: 100,
        dataIndex: 'ibAccountId'
    }, {
        text: 'Strategy',
        width: 100,
        dataIndex: 'strategyId',
        renderer: 'strategyRenderer'
    }, {
        text: 'PermId',
        width: 100,
        dataIndex: 'ibPermId',
        align: 'right'
    }, {
        text: 'Undl',
        width: 80,
        dataIndex: 'underlying'
    }, {
        text: 'Cur',
        width: 60,
        dataIndex: 'currency'
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
        text: 'Symbol',
        width: 180,
        dataIndex: 'symbol'
    }, {
        text: 'Sec',
        width: 60,
        dataIndex: 'secType'
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
        align: 'right'
    }, {
        text: 'HB',
        width: 60,
        dataIndex: 'heartbeatCount',
        align: 'right'
    }, {
        text: 'Submit',
        width: 60,
        dataIndex: 'submitType',
        align: 'right'
    }, {
        text: 'Exchange',
        width: 100,
        dataIndex: 'exchange'
    }, {
        text: 'Trigger',
        flex: 1,
        dataIndex: 'triggerDesc'
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'ordersPaging',
        bind: '{ibOrders}',
        dock: 'bottom',
        displayInfo: true
    }]
});