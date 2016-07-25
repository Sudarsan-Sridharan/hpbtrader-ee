/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('HtrGui.view.main.MainController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.main',

    setGlyphs: function() {
        var me = this;

        me.lookupReference('mktDataPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('mktdata'));
        me.lookupReference('strategyPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('strategy'));
        me.lookupReference('executionPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('exec'));
    }
});
