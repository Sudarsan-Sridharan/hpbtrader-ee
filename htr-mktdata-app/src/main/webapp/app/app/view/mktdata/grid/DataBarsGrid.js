/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('MktData.view.mktdata.grid.DataBarsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-mktdata-databars-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    bind: '{dataBars}',
    title: 'Data Bars',
    viewConfig: {
        stripeRows: true
    },
    columns: [{}],

    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'dataBarsPaging',
        bind: '{dataBars}',
        dock: 'bottom',
        displayInfo: true
    }]
});