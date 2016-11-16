/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('HtrGui.view.mktdata.MktDataController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'HtrGui.common.Definitions'
    ],

    alias: 'controller.htr-mktdata',

    init: function () {
        var me = this,
            ibAccounts = me.getStore('ibAccounts'),
            dataSeriesStore = me.getStore('dataSeriesStore'),
            rtDataStore = me.getStore('rtDataStore'),
            dataSeriesGrid;

        // strange bug, dataSeriesGrid reference ready only after some time
        setTimeout(function() {
            dataSeriesGrid = me.lookupReference('dataSeriesGrid');
            if (dataSeriesStore) {
                dataSeriesStore.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixMktData + '/dataseries');
                dataSeriesStore.load(function(records, operation, success) {
                    if (success) {
                        console.log('loaded dataSeriesStore');
                        dataSeriesGrid.setSelection(dataSeriesStore.first());
                    }
                });
            }
        }, 100);

        if (ibAccounts) {
            ibAccounts.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixMktData + '/ibaccounts');
            ibAccounts.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded ibAccounts');
                }
            });
        }
        if (rtDataStore) {
            rtDataStore.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixMktData + '/dataseries/rtdata');
            rtDataStore.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded rtDataStore');
                }
            });
        }

        var ws = new WebSocket(HtrGui.common.Definitions.wsUrlMktData);
        ws.onopen = function(evt) {
            console.log('WS mktdata opened');
        };
        ws.onclose = function(evt) {
            console.log('WS mktdata closed');
        };
        ws.onmessage = function(evt) {
            var msg = evt.data,
                arr = msg.split(",");

            if (arr[0] == 'rt') {
                me.updateRtData(msg);
            } else if (arr[0] == 'dataBar') {
                console.log('WS mktdata message: ' + msg);
                if (me.dataSeriesId == arr[3]) {
                    me.reloadDataBars();
                }
            }
        };
        ws.onerror = function(evt) {
            console.log('WS mktdata error');
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

    reloadDataBars: function() {
        var me = this,
            dataBars = me.getStore('dataBars');

        if (dataBars.isLoaded()) {
            dataBars.reload();
        } else {
            dataBars.load(function(records, operation, success) {
                if (success) {
                    console.log('loaded dataBars for dataSeriesId=' + me.dataSeriesId)
                }
            });
        }
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
            rtDataStore = me.getStore('rtDataStore'),
            box = Ext.MessageBox.wait(((con ? 'Connecting' : 'Disconnecting') + ' IB account ' + accountId), 'Action in progress');

        Ext.Ajax.request({
            method: 'PUT',
            url: HtrGui.common.Definitions.urlPrefixMktData + '/ibaccounts/' + accountId + '/connect/' + (con ? 'true' : 'false'),
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
            dataBarsGrid =  me.lookupReference('dataBarsGrid'),
            dataBarsPaging = me.lookupReference('dataBarsPaging');

        me.dataSeriesId = record.data.id;
        dataBars.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixMktData + '/dataseries/' + me.dataSeriesId  + '/pageddatabars');

        dataBars.load(function (records, operation, success) {
            if (success) {
                console.log('loaded dataBars for dataSeriesId=' + me.dataSeriesId)
            }
        });
    },

    toggleRtData: function(button, evt) {
        var me = this,
            dataSeriesId = button.getWidgetRecord().data.id,
            rtDataStore = me.getStore('rtDataStore');

        Ext.Ajax.request({
            method: 'PUT',
            url: HtrGui.common.Definitions.urlPrefixMktData + '/dataseries/' + dataSeriesId + '/rtdata/toggle',
            success: function(response) {
                rtDataStore.reload();
            }
        });
    },

    backfillDataBars: function(button, evt) {
        var dataSeriesId = button.getWidgetRecord().data.id;

        Ext.Ajax.request({
            method: 'PUT',
            url: HtrGui.common.Definitions.urlPrefixMktData + '/dataseries/' + dataSeriesId + '/backfill'
        });
    },

    deleteDataSeries: function(button, evt) {
        var me = this,
            dataSeriesStore = me.getStore('dataSeriesStore'),
            dataSeriesGrid = me.lookupReference('dataSeriesGrid'),
            dataSeriesId = button.getWidgetRecord().data.id,
            dataBars = me.getStore('dataBars');

        Ext.Msg.show({
            title: 'Delete data series, id=' + dataSeriesId + '?',
            message: 'All data bars will be deleted',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        method: 'DELETE',
                        url: HtrGui.common.Definitions.urlPrefixMktData + '/dataseries/' + dataSeriesId,
                        success: function(response, opts) {
                            dataSeriesStore.load(function(records, operation, success) {
                                if (success) {
                                    if (dataSeriesStore.getCount() == 0) {
                                        dataBars.removeAll();
                                    } else {
                                        dataSeriesGrid.setSelection(dataSeriesStore.first());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }
});