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
        var me = this,
            strategies = me.getStore('strategies'),
            strategiesGrid = me.lookupReference('strategiesGrid');

        me.prepare(function() {
            if (strategies) {
                strategies.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixExec + '/strategies');
                strategies.load(function (records, operation, success) {
                    if (success) {
                        console.log('loaded ibAccounts')
                        strategiesGrid.setSelection(strategies.first());
                    }
                });
            }
        });
    },

    prepare: function(callback) {
        var me = this;

        var ajaxQueue = function(step) {
            switch(step) {
                case 0: Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/iborderstatus/texts',
                    success: function (response, opts) {
                        me.ibOrderStatusTexts = Ext.decode(response.responseText);
                        ajaxQueue(1);
                    }}); break;
                case 1: Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/iborderstatus/colors',
                    success: function (response, opts) {
                        me.ibOrderStatusColors = Ext.decode(response.responseText);
                        ajaxQueue(2);
                    }}); break;
                case 2: Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/strategymode/colors',
                    success: function (response, opts) {
                        me.strategyModeColors = Ext.decode(response.responseText);
                        ajaxQueue(3);
                    }}); break;
                case 3: Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/tradetype/texts',
                    success: function (response, opts) {
                        me.tradeTypeTexts = Ext.decode(response.responseText);
                        ajaxQueue(4);
                    }}); break;
                case 4: Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/tradetype/colors',
                    success: function (response, opts) {
                        me.tradeTypeColors = Ext.decode(response.responseText);
                        ajaxQueue(5);
                    }}); break;
                case 5: Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/tradestatus/texts',
                    success: function (response, opts) {
                        me.tradeStatusTexts = Ext.decode(response.responseText);
                        ajaxQueue(6);
                    }}); break;
                case 6: Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/tradestatus/colors',
                    success: function (response, opts) {
                        me.tradeStatusColors = Ext.decode(response.responseText);
                        callback();
                    }}); break;
            }
        };
        ajaxQueue(0);
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

    onStrategySelect: function(grid, record, index, eOpts) {
        // TODO
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
