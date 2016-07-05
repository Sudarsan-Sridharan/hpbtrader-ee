/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.strategy.TradeIbOrder', {
    extend: 'HtrGui.model.strategy.Base',

    fields: [
        {name: 'tradeId', type: 'string', reference: {type: 'Trade', inverse: 'tradeIbOrders'}},
        'quantity',
        'status',

        {name: 'ibPermId', mapping: 'ibOrder.ibPermId'},
        {name: 'ibOrderId', mapping: 'ibOrder.ibOrderId'},
        {name: 'strategyMode', mapping: 'ibOrder.strategyMode'},
        {name: 'triggerDesc', mapping: 'ibOrder.triggerDesc'},
        {name: 'submitType', mapping: 'ibOrder.submitType'},
        {name: 'orderAction', mapping: 'ibOrder.orderAction'},
        {name: 'quantity', mapping: 'ibOrder.quantity'},
        {name: 'orderType', mapping: 'ibOrder.orderType'},
        {name: 'limitPrice', mapping: 'ibOrder.limitPrice'},
        {name: 'stopPrice', mapping: 'ibOrder.stopPrice'},
        {name: 'fillPrice', mapping: 'ibOrder.fillPrice'},
        {name: 'status', mapping: 'ibOrder.status'},
        {name: 'createdDate', type: 'date', dateFormat: 'time', mapping: 'ibOrder.createdDate'},
        {name: 'strategyId', mapping: 'ibOrder.strategyId'},
        {name: 'symbol', mapping: 'ibOrder.symbol'}
    ]
});