/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('MktData.view.mktdata.grid.DataSeriesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-mktdata-dataseries-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    bind: '{dataSeriesStore}',
    title: 'Data Series',
    viewConfig: {
        stripeRows: true
    },
    listeners: {
        select: 'onDataSeriesSelect'
    },
    columns: [{
        text: '#',
        width: 60,
        dataIndex: 'displayOrder'
    }, {
        text: 'ID',
        width: 60,
        dataIndex: 'id'
    }, {
        text: 'Sec',
        width: 60,
        dataIndex: 'secType'
    }, {
        text: 'Undl',
        width: 80,
        dataIndex: 'underlying'
    }, {
        text: 'Cur',
        width: 60,
        dataIndex: 'currency'
    }, {
        text: 'Symbol',
        width: 180,
        dataIndex: 'symbol'
    }, {
        text: 'Exchange',
        width: 120,
        dataIndex: 'exchange'
    }, {
        text: 'Interval',
        width: 80,
        dataIndex: 'interval'
    }, {
        text: 'Active',
        width: 60,
        dataIndex: 'active',
        xtype: 'checkcolumn'
    }, {
        text: 'Alias',
        flex: 1,
        dataIndex: 'alias'
    }, {
        xtype: 'widgetcolumn',
        width: 50,
        widget: {
            xtype: 'button',
            width: 30,
            tooltip: 'Toggle RT Data',
            glyph: MktData.common.Glyphs.getGlyph('rss'),
            handler: 'toggleRtData'
        }
    }, {
        xtype: 'widgetcolumn',
        width : 50,
        widget: {
            xtype: 'button',
            width: 30,
            tooltip: 'Backfill Data Bars',
            glyph: MktData.common.Glyphs.getGlyph('longarrowleft'),
            handler: 'backfillDataBars'
        }
    }],

    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'dataSeriesPaging',
        bind: '{dataSeriesStore}',
        dock: 'bottom',
        displayInfo: true
    }]
});