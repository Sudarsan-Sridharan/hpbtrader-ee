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
        {name: 'symbol', mapping: 'tradeInstrument.symbol'},
        {name: 'underlying', mapping: 'tradeInstrument.underlying'},
        {name: 'secType', mapping: 'tradeInstrument.secType'},
        {name: 'currency', mapping: 'tradeInstrument.currency'},
        {name: 'exchange', mapping: 'tradeInstrument.exchange'}
    ]
});