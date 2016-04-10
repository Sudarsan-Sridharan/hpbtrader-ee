/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('MktData.view.mktdata.MktDataController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'MktData.common.Definitions'
    ],

    alias: 'controller.htr-mktdata',

    init: function () {
        var me = this,
            ibAccounts = me.getStore('ibAccounts'),
            dataSeries = me.getStore('dataSeries');

        if (ibAccounts) {
            ibAccounts.getProxy().setUrl(MktData.common.Definitions.urlPrefix + '/ibaccounts');
            ibAccounts.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded ibAccounts');
                }
            });
        }

        if (dataSeries) {
            dataSeries.getProxy().setUrl(MktData.common.Definitions.urlPrefix + '/series');
            dataSeries.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded dataSeries');
                }
            });
        }
    },

    connectStatusRenderer: function(val, metadata, record) {
        if (metadata) {
            metadata.style = 'background-color: ' + (val ? 'green' : 'red') + '; color: white;';
        }
        return (val ? 'conn' : 'disconn');
    },

    connectIb: function(grid, rowIndex, colIndex) {
        this.connect(grid, rowIndex, colIndex, true);
    },

    disconnectIb: function(grid, rowIndex, colIndex) {
        this.connect(grid, rowIndex, colIndex, false);
    },

    connect: function(grid, rowIndex, colIndex, con) {
        var me = this,
            ibAccounts = me.getStore('ibAccounts'),
            accountId = grid.getStore().getAt(rowIndex).get('accountId'),
            box = Ext.MessageBox.wait(((con ? 'Connecting' : 'Disconnecting') + ' IB account ' + accountId), 'Action in progress');

        Ext.Ajax.request({
            method: 'PUT',
            url: MktData.common.Definitions.urlPrefix + '/ibaccounts/' + accountId + '/connect/' + (con ? 'true' : 'false'),
            success: function(response) {
                box.hide();
                grid.getStore().reload();
            },
            failure: function() {
                box.hide();
            }
        });
    }
});