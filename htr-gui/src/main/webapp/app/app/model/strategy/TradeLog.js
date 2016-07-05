/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.strategy.TradeLog', {
    extend: 'HtrGui.model.strategy.Base',

    fields: [
        'tradeId',
        {name: 'logDate', type: 'date', dateFormat: 'time'},
        'tradePosition',
        'stopLoss',
        'price',
        'profitTarget',
        'unrealizedPl',
        'realizedPl',
        'tradeStatus'
    ]
});