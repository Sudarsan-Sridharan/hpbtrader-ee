/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.strategy.IbOrderEvent', {
    extend: 'HtrGui.model.strategy.Base',

    fields: [
        {name: 'ibOrderId', type: 'string', reference: {type: 'IbOrder', inverse: 'ibOrderEvents'}},
        {name: 'eventDate', type: 'date', dateFormat: 'time'},
        'status'
    ]
});