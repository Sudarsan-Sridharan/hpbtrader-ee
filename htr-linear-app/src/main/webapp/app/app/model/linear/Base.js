/**
 * Created by robertk on 11/14/15.
 */
Ext.define('Linear.model.linear.Base', {
    extend: 'Ext.data.Model',

    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'}
    ],
    schema: {
        id: 'linearSchema',
        namespace: 'Linear.model.linear'  // generate auto entityName
    }
});