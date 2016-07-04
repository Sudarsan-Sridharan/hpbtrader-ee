/**
 * Created by robertk on 4.4.2016.
 */
Ext.define('HtrGui.model.exec.Base', {
    extend: 'Ext.data.Model',

    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'}
    ],
    schema: {
        id: 'mktdataSchema',
        namespace: 'HtrGui.model.exec',  // generate auto entityName,
        proxy: {
            type: 'ajax',
            actionMethods: {
                read: 'GET',
                update: 'PUT'
            },
            reader: {
                type: 'json',
                rootProperty: 'items',
                totalProperty: 'total'
            },
            writer: {
                type: 'json',
                writeAllFields: true,
                writeRecordId: true
            },
            listeners: {
                exception: function(proxy, response, operation) {
                    //HtrGui.common.Util.showErrorMsg(response.responseText);
                }
            }
        }
    }
});