/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.strategy.IbOrder', {
    extend: 'HtrGui.model.strategy.Base',

    fields: [
        {name: 'createdDate', type: 'date', dateFormat: 'time'},
        'strategyId',
        'ibAccountId',
        'ibPermId',
        'ibOrderId',
        'strategyMode',
        'triggerDesc',
        'submitType',
        'orderAction',
        'quantity',
        'orderType',
        'orderPrice',
        'fillPrice',
        'status',
        {name: 'statusDate', type: 'date', dateFormat: 'time'},
        'heartbeatCount',
        {name: 'symbol', mapping: 'instrument.symbol'},
        {name: 'underlying', mapping: 'instrument.underlying'},
        {name: 'secType', mapping: 'instrument.secType'},
        {name: 'currency', mapping: 'instrument.currency'},
        {name: 'exchange', mapping: 'instrument.exchange'}
    ]
});