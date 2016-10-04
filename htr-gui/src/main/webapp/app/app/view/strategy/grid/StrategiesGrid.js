/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.StrategiesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-strategies-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    listeners: {
        select: 'onStrategySelect'
    },
    bind: '{strategies}',
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        text: 'ID',
        width: 80,
        dataIndex: 'id'
    }, {
        text: 'Active',
        width: 60,
        dataIndex: 'active',
        xtype: 'checkcolumn',
        disabled: true,
        disabledCls : '' // or don't add this config if you want the field to look disabled
    }, {
        text: 'IB Account',
        width: 120,
        dataIndex: 'ibAccountId'
    }, {
        text: 'Type',
        width: 120,
        dataIndex: 'strategyType',
        renderer: function(val, metadata, record) {
            return val.toLowerCase();
        }
    }, {
        text: 'Mode',
        width: 80,
        dataIndex: 'strategyMode',
        renderer: 'strategyModeRendererStrategy'
    }, {
        text: 'Params',
        width: 100,
        dataIndex: 'params'
    }, {
        text: 'Quant',
        width: 80,
        dataIndex: 'tradingQuantity',
        align: 'right'
    }, {
        text: 'Pos',
        width: 80,
        dataIndex: 'currentPosition',
        align: 'right'
    }, {
        text: 'All-Fil',
        width: 100,
        dataIndex: 'numAllOrders',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numAllOrders'] + '-' + record.data['numFilledOrders'];
        }
    }, {
        text: 'Sho-Lo',
        width: 100,
        dataIndex: 'numShorts',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numShorts'] + '-' + record.data['numLongs'];
        }
    }, {
        text: 'Win-Los',
        width: 100,
        dataIndex: 'numWinners',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numWinners'] + '-' + record.data['numLosers'];
        }
    }, {
        text: 'Trade Instrument',
        width: 250,
        dataIndex: 'symbol',
        renderer: function(val, metadata, record) {
            return (record.data['symbol'] + '-' + record.data['underlying'] + '-' + record.data['currency'] + '-' + record.data['secType'] + '-' + record.data['exchange']).toLowerCase();
        }
    }, {
        text: 'PL',
        width: 100,
        dataIndex: 'cumulativePl',
        align: 'right',
        renderer: function(val, metadata, record) {
            metadata.style = val < 0 ? 'color: red;' : 'color: green;';
            return Ext.util.Format.number(val, '0.00');
        }
    }, {
        text: 'Input Series',
        flex: 1,
        dataIndex: 'inputSeriesAliases',
        renderer: function(val, metadata, record) {
            metadata.tdAttr = 'data-qtip="' + val.toLowerCase() + '"';
            return val.toLowerCase();
        }
    }, {
        xtype: 'widgetcolumn',
        width : 50,
        widget: {
            xtype: 'button',
            width: 30,
            tooltip: 'Delete Strategy',
            glyph: HtrGui.common.Glyphs.getGlyph('fa_trash'),
            handler: 'deleteStrategy'
        }
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'strategiesPaging',
        bind: '{strategies}',
        dock: 'bottom',
        displayInfo: true
    }]
});