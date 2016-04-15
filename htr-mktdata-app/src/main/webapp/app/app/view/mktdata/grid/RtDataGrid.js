/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('MktData.view.mktdata.grid.RtDataGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-mktdata-rtdata-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    bind: '{rtDataStore}',
    title: 'RT Data',
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        text: 'Symbol',
        width: 180,
        dataIndex: 'symbol'
    }, {
        text: 'Sec',
        width: 60,
        dataIndex: 'secType'
    }, {
        text: 'Exchange',
        width: 120,
        dataIndex: 'exchange'
    }, {
        text: 'Bid Size',
        width: 120,
        dataIndex: 'bidSizeValue',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.tdCls = 'htr-' + record.data.dataSeriesId + ' htr-' + record.data.bidSizeFieldName.replace('_', '-').toLowerCase() + ' htr-' + record.data.bidSizeStatus.toLowerCase();
            return val;
        }
    }, {
        text: 'Bid',
        width: 120,
        dataIndex: 'bidValue',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.tdCls = 'htr-' + record.data.dataSeriesId + ' htr-' + record.data.bidFieldName.replace('_', '-').toLowerCase() + ' htr-' + record.data.bidStatus.toLowerCase();
            return val;
        }
    }, {
        text: 'Ask',
        width: 120,
        dataIndex: 'askValue',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.tdCls = 'htr-' + record.data.dataSeriesId + ' htr-' + record.data.askFieldName.replace('_', '-').toLowerCase() + ' htr-' + record.data.askStatus.toLowerCase();
            return val;
        }
    }, {
        text: 'Ask Size',
        width: 120,
        dataIndex: 'askSizeValue',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.tdCls = 'htr-' + record.data.dataSeriesId + ' htr-' + record.data.askSizeFieldName.replace('_', '-').toLowerCase() + ' htr-' + record.data.askSizeStatus.toLowerCase();
            return val;
        }
    }, {
        text: 'Last',
        width: 120,
        dataIndex: 'lastValue',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.tdCls = 'htr-' + record.data.dataSeriesId + ' htr-' + record.data.lastFieldName.replace('_', '-').toLowerCase() + ' htr-' + record.data.lastStatus.toLowerCase();
            return val;
        }
    }, {
        text: 'Last Size',
        width: 120,
        dataIndex: 'lastSizeValue',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.tdCls = 'htr-' + record.data.dataSeriesId + ' htr-' + record.data.lastSizeFieldName.replace('_', '-').toLowerCase() + ' htr-' + record.data.lastSizeStatus.toLowerCase();
            return val;
        }
    }, {
        text: 'Volume',
        width: 120,
        dataIndex: 'volumeValue',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.tdCls = 'htr-' + record.data.dataSeriesId + ' htr-' + record.data.volumeFieldName.replace('_', '-').toLowerCase() + ' htr-' + record.data.volumeStatus.toLowerCase();
            return val;
        }
    }, {
        text: 'Close',
        width: 120,
        dataIndex: 'closeValue',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.tdCls = 'htr-' + record.data.dataSeriesId + ' htr-' + record.data.closeFieldName.replace('_', '-').toLowerCase() + ' htr-' + record.data.closeStatus.toLowerCase();
            return val;
        }
    }, {
        text: 'Change',
        width: 120,
        dataIndex: 'changePctStr',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.tdCls = 'htr-' + record.data.dataSeriesId + ' htr-' + record.data.changePctFieldName.replace('_', '-').toLowerCase() + ' htr-' + record.data.changePctStatus.toLowerCase();
            return val;
        }
    }, {
        flex: 1
    }],

    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'rtDataPaging',
        bind: '{rtDataStore}',
        dock: 'bottom',
        displayInfo: true
    }]
});