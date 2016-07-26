/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.strategy.StrategyPerformance', {
    extend: 'HtrGui.model.strategy.Base',

    fields: [
        'strategyId',
        {name: 'performanceDate', type: 'date', dateFormat: 'time'},
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