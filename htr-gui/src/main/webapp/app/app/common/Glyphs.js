/**
 * Created by robertk on 9/6/15.
 */
Ext.define('HtrGui.common.Glyphs', {
    singleton: true,

    config: {
        webFont: 'FontAwesome',
        fa_plus: 'xf067',
        fa_pencil: 'xf040',
        fa_trash: 'xf1f8',
        fa_check: 'xf00c',
        fa_rotateleft: 'xf0e2',
        fa_refresh: 'xf021',
        fa_bar_chart: 'xf080',
        fa_list_ol: 'xf0cb',
        fa_money: 'xf0d6',
        fa_cog: 'xf013',
        fa_feed: 'xf09e',
        fa_long_arrow_left: 'xf177',
        fa_area_chart: 'xf1fe',
        fa_cogs: 'xf085',
        fa_exchange: 'xf0ec',
        fa_sort_amount_asc: 'xf160'
    },

    constructor: function(config) {
        this.initConfig(config);
    },

    getGlyph: function(glyph) {
        var me = this,
            font = me.getWebFont();
        if (typeof me.config[glyph] === 'undefined') {
            return false;
        }
        return me.config[glyph] + '@' + font;
    }
});