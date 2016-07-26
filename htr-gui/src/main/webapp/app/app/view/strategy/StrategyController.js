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
        var me = this;

        Ext.Ajax.request({
            url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/iborderstatus/texts',
            success: function(response, opts) {
                me.ibOrderStatusTexts = Ext.decode(response.responseText);
                Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/iborderstatus/colors',
                    success: function(response, opts) {
                        me.ibOrderStatusColors = Ext.decode(response.responseText);
                        Ext.Ajax.request({
                            url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/strategymode/colors',
                            success: function(response, opts) {
                                me.strategyModeColors = Ext.decode(response.responseText);
                                Ext.Ajax.request({
                                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/tradetype/texts',
                                    success: function(response, opts) {
                                        me.tradeTypeTexts = Ext.decode(response.responseText);
                                        Ext.Ajax.request({
                                            url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/tradetype/colors',
                                            success: function(response, opts) {
                                                me.tradeTypeColors = Ext.decode(response.responseText);
                                                Ext.Ajax.request({
                                                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/tradestatus/texts',
                                                    success: function(response, opts) {
                                                        me.tradeStatusTexts = Ext.decode(response.responseText);
                                                        Ext.Ajax.request({
                                                            url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/tradestatus/colors',
                                                            success: function(response, opts) {
                                                                me.tradeStatusColors = Ext.decode(response.responseText);
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    },

    ibOrderStatusRenderer: function(val, metadata, record) {
        var me = this;

        metadata.style = 'cursor: pointer; background-color: ' + me.ibOrderStatusColors[val] + '; color: white;';
        return me.ibOrderStatusTexts[val];
    },

    strategyRenderer: function(val, metadata, record) {
        var me = this;

        metadata.style = 'color: ' + me.strategyModeColors[val];
        return record.data['strategyId'] + '/' + val;
    },

    tradeTypeRenderer: function(val, metadata, record) {
        var me = this;

        metadata.style = 'color: ' + me.tradeTypeColors[val];
        return me.tradeTypeTexts[val];
    },

    tradeStatusRenderer: function(val, metadata, record) {
        var me = this;

        metadata.style = 'cursor: pointer; color: white; background-color: ' + me.tradeStatusColors[val];
        return me.tradeStatusTexts[val];
    },

    setGlyphs: function() {
        var me = this;

        me.lookupReference('strategyLogsPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('fa_sort_amount_asc'));
        me.lookupReference('ibOrdersPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('fa_list_ol'));
        me.lookupReference('tradesPanel').setGlyph(HtrGui.common.Glyphs.getGlyph('fa_money'));
    },

    showTradeIbOrders: function() {
        // TODO
    }
});
