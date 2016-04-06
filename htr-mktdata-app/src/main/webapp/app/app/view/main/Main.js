/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('MktData.view.main.Main', {
    extend: 'Ext.container.Container',
    requires: [
        'Ext.button.Button',
        'Ext.layout.container.VBox',
        'MktData.common.Glyphs',
        'MktData.view.mktdata.MktData',
        'MktData.view.main.MainController',
        'MktData.view.main.MainModel'
    ],

    xtype: 'app-main',

    controller: 'main',
    viewModel: {
        type: 'main'
    },
    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'htr-mktdata'
    }]
});
