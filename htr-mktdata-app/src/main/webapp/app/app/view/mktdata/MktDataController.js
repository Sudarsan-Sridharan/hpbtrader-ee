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
            dataSeriesStore = me.getStore('dataSeriesStore'),
            rtDataStore = me.getStore('rtDataStore'),
            dataSeriesGrid = me.lookupReference('dataSeriesGrid');

        if (ibAccounts) {
            ibAccounts.getProxy().setUrl(MktData.common.Definitions.urlPrefix + '/ibaccounts');
            ibAccounts.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded ibAccounts');
                }
            });
        }
        if (dataSeriesStore) {
            dataSeriesStore.getProxy().setUrl(MktData.common.Definitions.urlPrefix + '/dataseries');
            dataSeriesStore.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded dataSeriesStore');
                    dataSeriesGrid.setSelection(dataSeriesStore.first());
                }
            });
        }
        if (rtDataStore) {
            rtDataStore.getProxy().setUrl(MktData.common.Definitions.urlPrefix + '/dataseries/rtdata');
            rtDataStore.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded rtDataStore');
                }
            });
        }

        var ws = new WebSocket(MktData.common.Definitions.wsUrl);
        ws.onopen = function(evt) {
            console.log('WS opened');
        };
        ws.onclose = function(evt) {
            console.log('WS closed');
        };
        ws.onmessage = function(evt) {
            var msg = evt.data;
            if (msg.substring(0, 2) === 'rt') {
                me.updateRtData(msg);
            }
            //console.log('WS message, content=' + evt.data);
        };
        ws.onerror = function(evt) {
            console.log('WS error');
        };
    },

    updateRtData: function(msg) {
        var arr = msg.split(","),
            dataSeriesId = arr[1],
            symbol = arr[2],
            fieldName = arr[3],
            fieldValue = arr[4],
            fieldStatus = arr[5];

        var selector = 'td.htr-' + dataSeriesId + '.htr-' + fieldName.replace('_', '-').toLowerCase();
        var td = Ext.query(selector)[0];
        if (td) {
            td.classList.remove('htr-uptick');
            td.classList.remove('htr-downtick');
            td.classList.remove('htr-unchanged');
            td.classList.remove('htr-positive');
            td.classList.remove('htr-negative');
            td.classList.add('htr-' + fieldStatus.toLowerCase());

            var div = Ext.query('div', true, td)[0];
            if (div) {
                div.innerHTML = fieldValue;
            }
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
            rtDataStore = me.getStore('rtDataStore');
            box = Ext.MessageBox.wait(((con ? 'Connecting' : 'Disconnecting') + ' IB account ' + accountId), 'Action in progress');

        Ext.Ajax.request({
            method: 'PUT',
            url: MktData.common.Definitions.urlPrefix + '/ibaccounts/' + accountId + '/connect/' + (con ? 'true' : 'false'),
            success: function(response) {
                box.hide();
                grid.getStore().reload();
                rtDataStore.reload();
            },
            failure: function() {
                box.hide();
            }
        });
    },

    onDataSeriesSelect: function(grid, record, index, eOpts) {
        var me = this,
            dataBars = me.getStore('dataBars'),
            dataBarsPaging = me.lookupReference('dataBarsPaging');

        me.dataSeriesId = record.data.id;
        dataBars.getProxy().setUrl(MktData.common.Definitions.urlPrefix + '/dataseries/' + me.dataSeriesId  + '/pagedbars');

        if (dataBarsPaging.getStore().isLoaded()) {
            dataBarsPaging.moveFirst();
        } else {
            dataBars.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded dataBars for dataSeriesId=' + me.dataSeriesId)
                }
            });
        }
    },

    toggleRtData: function(button, evt) {
        var me = this,
            dataSeriesId = button.getWidgetRecord().data.id,
            rtDataStore = me.getStore('rtDataStore');

        Ext.Ajax.request({
            method: 'PUT',
            url: MktData.common.Definitions.urlPrefix + '/dataseries/' + dataSeriesId + '/rtdata/toggle',
            success: function(response) {
                rtDataStore.reload();
            }
        });
    },

    backfillDataBars: function(button, evt) {
        var dataSeriesId = button.getWidgetRecord().data.id;

        Ext.Ajax.request({
            method: 'PUT',
            url: MktData.common.Definitions.urlPrefix + '/dataseries/' + dataSeriesId + '/backfill'
        });
    }
});