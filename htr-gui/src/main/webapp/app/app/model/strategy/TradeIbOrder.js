/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.strategy.TradeIbOrder', {
    extend: 'HtrGui.model.strategy.Base',

    fields: [
        {name: 'tradeId', type: 'string', reference: {type: 'Trade', inverse: 'tradeIbOrders'}},
        'quantity',
        'ibOrderQuantity',
        'ibOrderInfo'
    ]
});