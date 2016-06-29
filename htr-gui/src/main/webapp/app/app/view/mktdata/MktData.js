/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('HtrGui.view.mktdata.MktData', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.layout.container.VBox',
        'HtrGui.view.mktdata.MktDataController',
        'HtrGui.view.mktdata.MktDataModel',
        'HtrGui.view.mktdata.grid.IbAccountsGrid',
        'HtrGui.view.mktdata.grid.DataSeriesGrid',
        'HtrGui.view.mktdata.grid.RtDataGrid',
        'HtrGui.view.mktdata.grid.DataBarsGrid',
        'HtrGui.common.Glyphs',
        'Ext.tab.Panel'
    ],

    xtype: 'htr-mktdata',
    header: false,
    border: false,
    controller: 'htr-mktdata',
    viewModel: {
        type: 'htr-mktdata'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'htr-mktdata-ibaccounts-grid',
        reference: 'ibAccountsGrid'
    }, {
        xtype: 'htr-mktdata-dataseries-grid',
        reference: 'dataSeriesGrid'
    }, {
        xtype: 'htr-mktdata-rtdata-grid',
        reference: 'rtDataGrid'
    }, {
        xtype: 'htr-mktdata-databars-grid',
        reference: 'dataBarsGrid'
    }]
});