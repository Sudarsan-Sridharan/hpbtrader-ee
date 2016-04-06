/**
 * The main application class. An instance of this class is created by app.js when it calls
 * Ext.application(). This is the ideal place to handle application launch and initialization
 * details.
 */
Ext.define('MktData.Application', {
    extend: 'Ext.app.Application',

    requires: [
        'Ext.container.Viewport',
        'Ext.layout.container.Fit',
        'MktData.view.main.Main'
    ],

    name: 'MktData',

    stores: [
    ],

    launch: function () {
        var link = document.createElement('link');
        link.type = 'image/ico';
        link.rel = 'icon';
        link.href = 'resources/images/favicon.ico';
        document.getElementsByTagName('head')[0].appendChild(link);

        var main = Ext.create('MktData.view.main.Main');

        var viewport = Ext.create('Ext.container.Viewport', {
            layout: 'fit'
        });
        viewport.add(main);
    }
});
