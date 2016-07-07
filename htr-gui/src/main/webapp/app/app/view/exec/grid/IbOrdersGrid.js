/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.exec.grid.IbOrdersGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-exec-iborders-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    bind: '{ibOrders}',
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        text: 'ID',
        width: 120,
        dataIndex: 'id'
    }, {
        flex: 1
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'ibOrdersPaging',
        bind: '{ibOrders}',
        dock: 'bottom',
        displayInfo: true
    }]
});