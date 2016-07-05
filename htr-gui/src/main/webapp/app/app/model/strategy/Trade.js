/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.strategy.Trade', {
    extend: 'HtrGui.model.strategy.Base',

    fields: [
        'strategyId',
        'quantity',
        'tradePosition',
        {name: 'initOpenDate', type: 'date', dateFormat: 'time'},
        {name: 'closeDate', type: 'date', dateFormat: 'time'},
        'openPrice',
        'closePrice',
        'initialStop',
        'stopLoss',
        'profitTarget',
        'unrealizedPl',
        'realizedPl',
        'tradeType',
        'tradeStatus'
    ]
});