/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.model.exec.IbOrderEvent', {
    extend: 'HtrGui.model.exec.Base',

    fields: [
        {name: 'ibOrderId', type: 'string', reference: {type: 'IbOrder', inverse: 'events'}},
        {name: 'eventDate', type: 'date', dateFormat: 'time'},
        'status'
    ]
});