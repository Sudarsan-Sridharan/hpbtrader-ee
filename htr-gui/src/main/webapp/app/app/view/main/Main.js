/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('HtrGui.view.main.Main', {
    extend: 'Ext.tab.Panel',
    requires: [
        'Ext.button.Button',
        'Ext.layout.container.VBox',
        'HtrGui.common.Glyphs',
        'HtrGui.view.mktdata.MktData',
        'HtrGui.view.strategy.Strategy',
        'HtrGui.view.exec.Exec',
        'HtrGui.view.main.MainController',
        'HtrGui.view.main.MainModel',
        'HtrGui.model.strategy.Trade',
        'HtrGui.model.strategy.TradeIbOrder',
        'HtrGui.model.exec.IbOrder',
        'HtrGui.model.exec.IbOrderEvent'
    ],

    xtype: 'app-main',

    controller: 'main',
    viewModel: {
        type: 'main'
    },
    listeners: {
        beforerender: 'setGlyphs'
    },
    items: [{
        xtype: 'htr-mktdata',
        reference: 'mktDataPanel',
        title: 'Market Data'
    }, {
        xtype: 'htr-strategy',
        reference: 'strategyPanel',
        title: 'Strategy'
    }, {
        xtype: 'htr-exec',
        reference: 'executionPanel',
        title: 'Execution'
    }]
});
