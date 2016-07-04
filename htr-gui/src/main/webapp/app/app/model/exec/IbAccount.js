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
        {name: 'mdcAccounts', mapping: 'mktDataConnection.accounts'},
        {name: 'mdcIsConnected', mapping: 'mktDataConnection.isConnected'}
    ]
});