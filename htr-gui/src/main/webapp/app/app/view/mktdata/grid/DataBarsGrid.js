/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('HtrGui.view.mktdata.grid.DataBarsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-mktdata-databars-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    bind: '{dataBars}',
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        text: 'DSID',
        width: 60,
        dataIndex: 'dataSeriesId'
    }, {
        text: 'Bar Close Date',
        width: 180,
        dataIndex: 'barCloseDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Open',
        width: 100,
        dataIndex: 'barOpen',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'High',
        width: 100,
        dataIndex: 'barHigh',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'Low',
        width: 100,
        dataIndex: 'barLow',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'Close',
        width: 100,
        dataIndex: 'barClose',
        align: 'right',
        renderer: function(val, metadata, record) {
            return Ext.util.Format.number(val, '0.00###');
        }
    }, {
        text: 'Volume',
        width: 100,
        dataIndex: 'volume',
        align: 'right'
    }, {
        text: 'Count',
        width: 100,
        dataIndex: 'count',
        align: 'right'
    }, {
        text: 'Wap',
        width: 100,
        dataIndex: 'wap',
        align: 'right'
    }, {
        text: 'Has Gaps',
        flex: 1,
        dataIndex: 'hasGaps'
    }],

    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'dataBarsPaging',
        bind: '{dataBars}',
        dock: 'bottom',
        displayInfo: true
    }]
});