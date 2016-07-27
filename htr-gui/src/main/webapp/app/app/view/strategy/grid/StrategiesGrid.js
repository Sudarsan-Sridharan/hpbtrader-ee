/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.grid.StrategiesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'htr-strategy-strategies-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'HtrGui.view.strategy.StrategyController'
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
        dataIndex: 'id',
        renderer: function (val, metadata, record) {
            var color = ('true' === record.data['active'] ? 'green' : 'red');
            return '<span style="color: ' + color + ';">' + val + '</span>';
        }
    }, {
        text: 'IB Account',
        width: 120,
        dataIndex: 'ibAccountId'
    }, {
        text: 'Type',
        width: 80,
        dataIndex: 'strategyType'
    }, {
        text: 'Mode',
        width: 80,
        dataIndex: 'strategyMode'
    }, {
        text: 'Params',
        width: 80,
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
        width: 80,
        dataIndex: 'numAllOrders',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numAllOrders'] + '-' + record.data['numFilledOrders'];
        }
    }, {
        text: 'Sho-Lon',
        width: 80,
        dataIndex: 'numShorts',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numShorts'] + '-' + record.data['numLongs'];
        }
    }, {
        text: 'Win-Los',
        width: 80,
        dataIndex: 'numWinners',
        align: 'right',
        renderer: function(val, metadata, record) {
            return record.data['numWinners'] + '-' + record.data['numLosers'];
        }
    }, {
        text: 'Instrument',
        width: 200,
        dataIndex: 'symbol',
        renderer: function(val, metadata, record) {
            return record.data['symbol'] + '/' + record.data['underlying'] + '/' + record.data['secType'] + '/' + record.data['currency'] + '/' + record.data['exchange'];
        }
    }, {
        text: 'PL',
        width: 80,
        dataIndex: 'cumulativePl',
        align: 'right'
    }, {
        text: 'Input Series',
        flex: 1,
        dataIndex: 'inputSeriesAliases'
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        reference: 'strategiesPaging',
        bind: '{strategies}',
        dock: 'bottom',
        displayInfo: true
    }]
});