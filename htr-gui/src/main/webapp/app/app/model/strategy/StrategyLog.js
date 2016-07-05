/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.strategy.StrategyLog', {
    extend: 'HtrGui.model.strategy.Base',

    fields: [
        'strategyId',
        {name: 'logDate', type: 'date', dateFormat: 'time'},
        'active',
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
        'numLosers'
    ]
});