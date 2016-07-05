/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.strategy.Strategy', {
    extend: 'HtrGui.model.strategy.Base',

    fields: [
        'ibAccountId',
        'inputSeriesAliases',
        'strategyType',
        'active',
        'displayOrder',
        'strategyMode',
        'params',
        'tradingQuantity',
        'numAllOrders',
        'numFilledOrders',
        'currentPosition',
        'cumulativePl',
        'numShorts',
        'numLongs',
        'numWinners',
        'numLosers',
        {name: 'symbol', mapping: 'instrument.symbol'},
        {name: 'underlying', mapping: 'instrument.underlying'},
        {name: 'secType', mapping: 'instrument.secType'},
        {name: 'currency', mapping: 'instrument.currency'},
        {name: 'exchange', mapping: 'instrument.exchange'}
    ]
});