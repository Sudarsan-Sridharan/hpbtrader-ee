/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.StrategyController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'HtrGui.common.Definitions'
    ],

    alias: 'controller.htr-strategy',

    init: function() {

    },

    setGlyphs: function() {
        var me = this;

        me.lookupReference('strategyLogsPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('fa_sort_amount_asc'));
        me.lookupReference('ibOrdersPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('fa_list_ol'));
        me.lookupReference('tradesPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('fa_money'));
    }
});
