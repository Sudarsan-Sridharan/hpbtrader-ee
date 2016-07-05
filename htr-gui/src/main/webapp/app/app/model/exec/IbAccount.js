/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('HtrGui.model.exec.IbAccount', {
    extend: 'HtrGui.model.exec.Base',

    idProperty: 'accountId',

    fields: [
        'host',
        'port',
        'mktDataClientId',
        'execClientId',
        {name: 'ecAccounts', mapping: 'execConnection.accounts'},
        {name: 'ecConnected', mapping: 'execConnection.connected'}
    ]
});