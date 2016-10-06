/**
 * Created by robertk on 27.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.TradeIbOrdersGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-tradeiborders-grid',
    requires: [
        'Ext.grid.column.Date'
    ],
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        xtype: 'templatecolumn',
        text: 'ID',
        width: 120,
        dataIndex: 'id',
        tpl: '{tradeId}/{id}'
    }, {
        xtype: 'templatecolumn',
        text: 'Quantity',
        width: 130,
        dataIndex: 'quantity',
        align: 'right',
        tpl: '{quantity} ({ibOrderQuantity})'
    }, {
        text: 'Ib Order Info',
        flex: 1,
        dataIndex: 'ibOrderInfo'
    }]
});