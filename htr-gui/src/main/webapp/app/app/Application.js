/**
 * The main application class. An instance of this class is created by app.js when it calls
 * Ext.application(). This is the ideal place to handle application launch and initialization
 * details.
 */
Ext.define('HtrGui.Application', {
    extend: 'Ext.app.Application',

    requires: [
        'Ext.container.Viewport',
        'Ext.layout.container.Fit',
        'HtrGui.view.main.Main'
    ],

    name: 'HPB Trader',

    stores: [
    ],

    launch: function () {
        google.charts.load('current', {packages: ['corechart']});
        google.charts.setOnLoadCallback(console.log('Google charts loaded'));

        var link = document.createElement('link');
        link.type = 'image/ico';
        link.rel = 'icon';
        link.href = 'resources/images/favicon.ico';
        document.getElementsByTagName('head')[0].appendChild(link);

        var main = Ext.create('HtrGui.view.main.Main');

        var viewport = Ext.create('Ext.container.Viewport', {
            layout: 'fit'
        });
        viewport.add(main);
    }
});
