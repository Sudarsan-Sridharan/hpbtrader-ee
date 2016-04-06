/**
 * Created by robertk on 6.4.2016.
 */
Ext.define('MktData.view.mktdata.grid.IbAccountsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-mktdata-ibaccounts-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    bind: '{ibAccounts}',
    viewConfig: {
        stripeRows: true
    },
    columns: [{}],

    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'ibAccountsPaging',
        bind: '{ibAccounts}',
        dock: 'bottom',
        displayInfo: true
    }]
});