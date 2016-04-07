/**
 * Created by robertk on 6.4.2016.
 */
Ext.define('MktData.view.mktdata.grid.IbAccountsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-mktdata-ibaccounts-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    bind: '{ibAccounts}',
    title: 'IB Accounts',
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        text: 'Account ID',
        width: 120,
        dataIndex: 'accountId'
    }, {
        text: 'Connect',
        xtype: 'actioncolumn',
        width: 140,
        align: 'center',
        items: [{
            icon: 'resources/images/play-circle.png',
            tooltip: 'Connect',
            handler: 'connectIb'
        }, {
            icon: 'resources/images/pause.png',
            tooltip: 'Disconnect',
            handler: 'disconnectIb'
        }]
    }, {
        text: 'Status',
        width: 80,
        align: 'center',
        dataIndex: 'mdcIsConnected',
        renderer: 'connectStatusRenderer'
    }, {
        text: 'Accounts',
        width: 200,
        dataIndex: 'mdcAccounts'
    }, {
        text: 'Host',
        width: 150,
        dataIndex: 'host',
        editor: {
            xtype: 'textfield',
            allowBlank: false
        }
    }, {
        text: 'Port',
        width: 80,
        dataIndex: 'port',
        align: 'right',
        editor: {
            xtype: 'numberfield',
            minValue: 1,
            maxValue: 65535,
            allowDecimals: false
        }
    }, {
        text: 'Md Cli',
        width: 80,
        dataIndex: 'mktDataClientId',
        align: 'right',
        editor: {
            xtype: 'numberfield',
            minValue: 1,
            maxValue: 65535,
            allowDecimals: false
        }
    }, {
        text: 'Ex Cli',
        width: 80,
        dataIndex: 'execClientId',
        align: 'right',
        editor: {
            xtype: 'numberfield',
            minValue: 1,
            maxValue: 65535,
            allowDecimals: false
        }
    }, {
        flex: 1
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'ibAccountsPaging',
        bind: '{ibAccounts}',
        dock: 'bottom',
        displayInfo: true
    }]
});