/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.exec.ExecController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'HtrGui.common.Definitions',
        'HtrGui.view.exec.grid.IbOrderEventsGrid',
        'HtrGui.view.exec.window.IbOrderEventsWindow'
    ],

    alias: 'controller.htr-exec',

    init: function () {
        var me = this,
            ibAccounts = me.getStore('ibAccounts'),
            ibAccountsGrid = me.lookupReference('ibAccountsGrid');

        Ext.Ajax.request({
            url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/iborderstatus/texts',
            success: function(response, opts) {
                me.ibOrderStatusTexts = Ext.decode(response.responseText);
                Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/iborderstatus/colors',
                    success: function(response, opts) {
                        me.ibOrderStatusColors = Ext.decode(response.responseText);
                        Ext.Ajax.request({
                            url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/strategymode/colors',
                            success: function(response, opts) {
                                me.strategyModeColors = Ext.decode(response.responseText);
                                if (ibAccounts) {
                                    ibAccounts.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixExec + '/ibaccounts');
                                    ibAccounts.load(function (records, operation, success) {
                                        if (success) {
                                            console.log('loaded ibAccounts')
                                            ibAccountsGrid.setSelection(ibAccounts.first());
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });

        var ws = new WebSocket(HtrGui.common.Definitions.wsUrlExec);
        ws.onopen = function(evt) {
            console.log('WS exec opened');
        };
        ws.onclose = function(evt) {
            console.log('WS exec closed');
        };
        ws.onmessage = function(evt) {
            var msg = evt.data,
                arr = msg.split(",");

            console.log('WS exec message: ' + msg);
            if (arr[0] == 'ibOrder') {
                if (me.ibAccountId == arr[3]) {
                    me.reloadIbOrders();
                }
            }
        };
        ws.onerror = function(evt) {
            console.log('WS exec error');
        };
    },

    reloadIbOrders: function() {
        var me = this,
            ibOrders = me.getStore('ibOrders');

        if (ibOrders.isLoaded()) {
            ibOrders.reload();
        } else {
            ibOrders.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded ibOrders for ibAccountId=' + me.ibAccountId)
                }
            });
        }
    },

    onIbAccountSelect: function(grid, record, index, eOpts) {
        var me = this,
            ibOrders = me.getStore('ibOrders'),
            ordersPaging = me.lookupReference('ordersPaging');

        me.ibAccountId = record.data.accountId;
        ibOrders.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixExec + '/ibaccounts/' + me.ibAccountId  + '/iborders');

        if (ordersPaging.getStore().isLoaded()) {
            ordersPaging.moveFirst();
        } else {
            ibOrders.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded ibOrders for ibAccountId=' + me.ibAccountId)
                }
            });
        }
    },

    showIbOrderEvents: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex != 2) {
            return;
        }
        var me = this;

        if (!me.ibOrderEventsGrid) {
            me.ibOrderEventsGrid =  Ext.create('HtrGui.view.exec.grid.IbOrderEventsGrid');
            me.ibOrderEventsWindow = Ext.create('widget.htr-exec-iborderevents-window');
            me.ibOrderEventsWindow.add(me.ibOrderEventsGrid);
            me.getView().add(me.ibOrderEventsWindow);
        }
        me.ibOrderEventsGrid.setStore(record.ibOrderEvents());
        me.ibOrderEventsWindow.show();
    },

    ibOrderStatusRenderer: function(val, metadata, record) {
        var me = this;
        metadata.style = 'cursor: pointer; background-color: ' + me.ibOrderStatusColors[val] + '; color: white;';
        return me.ibOrderStatusTexts[val];
    },

    ibOrderStatusRendererEvents: function(val, metadata, record) {
        var me = this;
        metadata.style = 'background-color: ' + me.ibOrderStatusColors[val] + '; color: white;';
        return me.ibOrderStatusTexts[val];
    },

    strategyModeRenderer: function(val, metadata, record) {
        var me = this;
        metadata.style = 'color: ' + me.strategyModeColors[val];
        return val;
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
            url: HtrGui.common.Definitions.urlPrefixExec + '/ibaccounts/' + accountId + '/connect/' + (con ? 'true' : 'false'),
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