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
    bind: '{dataSeries}',
    viewConfig: {
        stripeRows: true
    },
    columns: [{}],

    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'dataSeriesPaging',
        bind: '{dataSeries}',
        dock: 'bottom',
        displayInfo: true
    }]
});