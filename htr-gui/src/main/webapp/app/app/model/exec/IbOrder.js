/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.exec.IbOrder', {
    extend: 'HtrGui.model.exec.Base',

    fields: [
        'ibPermId',
        'ibOrderId',
        'strategyMode',
        'triggerDesc',
        'submitType',
        'orderAction',
        'quantity',
        'orderType',
        'limitPrice',
        'stopPrice',
        'fillPrice',
        'status',
        {name: 'createdDate', type: 'date', dateFormat: 'time'},
        'strategyId',
        {name: 'symbol', mapping: 'instrument.symbol'},
        {name: 'underlying', mapping: 'instrument.underlying'},
        {name: 'secType', mapping: 'instrument.secType'},
        {name: 'currency', mapping: 'instrument.currency'},
        {name: 'exchange', mapping: 'instrument.exchange'}
    ]
});