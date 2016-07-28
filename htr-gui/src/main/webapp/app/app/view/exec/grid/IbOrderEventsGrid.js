/**
 * Created by robertk on 4/17/15.
 */
Ext.define('HtrGui.view.exec.grid.IbOrderEventsGrid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Date'
    ],

    disableSelection: true,
    header: false,
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        xtype: 'templatecolumn',
        text: 'ID',
        width: 100,
        dataIndex: 'id',
        tpl: '{ibOrderId}/{id}'
    }, {
        text: 'Event Date',
        width: 180,
        dataIndex: 'eventDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Status',
        width: 80,
        dataIndex: 'status',
        renderer: 'ibOrderStatusRenderer'
    }]
});