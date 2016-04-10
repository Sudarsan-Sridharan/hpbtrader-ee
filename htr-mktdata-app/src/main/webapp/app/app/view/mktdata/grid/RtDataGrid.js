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
    columns: [{}],

    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'rtDataPaging',
        bind: '{rtDataStore}',
        dock: 'bottom',
        displayInfo: true
    }]
});