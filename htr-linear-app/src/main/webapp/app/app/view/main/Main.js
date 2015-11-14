/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 */
Ext.define('Linear.view.main.Main', {
    extend: 'Ext.container.Container',

    requires: [
        'Linear.view.main.MainController',
        'Linear.view.main.MainModel',
        'Linear.view.linear.Linear'
    ],

    xtype: 'app-main',
    
    controller: 'main',
    viewModel: {
        type: 'main'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    scrollable: true,
    items: [{
        xtype: 'htr-linear'
    }]
});
