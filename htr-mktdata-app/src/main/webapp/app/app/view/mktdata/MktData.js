/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('MktData.view.mktdata.MktData', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.layout.container.VBox',
        'MktData.view.mktdata.MktDataController',
        'MktData.view.mktdata.MktDataModel',
        'MktData.view.mktdata.grid.IbAccountsGrid',
        'MktData.view.mktdata.grid.DataSeriesGrid',
        'MktData.view.mktdata.grid.RtDataGrid',
        'MktData.view.mktdata.grid.DataBarsGrid',
        'MktData.common.Glyphs',
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