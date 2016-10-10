/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.exec.Exec', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.layout.container.VBox',
        'HtrGui.view.exec.ExecController',
        'HtrGui.view.exec.ExecModel',
        'HtrGui.view.exec.grid.IbAccountsGrid',
        'HtrGui.view.exec.grid.IbOrdersGrid',
        'HtrGui.common.Glyphs',
        'Ext.tab.Panel'
    ],

    xtype: 'htr-exec',
    header: false,
    border: false,
    scrollable: true,
    controller: 'htr-exec',
    viewModel: {
        type: 'htr-exec'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'htr-exec-ibaccounts-grid',
        reference: 'ibAccountsGrid',
        title: 'IB Accounts'
    }, {
        xtype: 'htr-exec-iborders-grid',
        reference: 'ibOrdersGrid',
        title: 'IB Orders'
    }]
});