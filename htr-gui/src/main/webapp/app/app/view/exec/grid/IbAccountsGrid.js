/**
 * Created by robertk on 6.4.2016.
 */
Ext.define('HtrGui.view.exec.grid.IbAccountsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-exec-ibaccounts-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    listeners: {
        select: 'onIbAccountSelect'
    },
    bind: '{ibAccounts}',
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
        text: 'Ex Cli',
        width: 80,
        dataIndex: 'execClientId',
        align: 'right',
        editor: {
            xtype: 'numberfield',
            minValue: 1,
            maxValue: 65535,
            allowDecimals: false
        },
        renderer: function(val, metadata, record) {
            if (metadata) {
                metadata.style = 'background-color: ' + (record.get('ecConnected') ? 'green' : 'red') + '; color: white;';
            }
            return val;
        }
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
        text: 'Accounts',
        dataIndex: 'ecAccounts',
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