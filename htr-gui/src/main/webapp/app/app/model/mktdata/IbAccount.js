/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('HtrGui.model.mktdata.IbAccount', {
    extend: 'HtrGui.model.mktdata.Base',

    idProperty: 'accountId',

    fields: [
        'host',
        'port',
        'mktDataClientId',
        'execClientId',
        {name: 'mdcAccounts', mapping: 'mktDataConnection.accounts'},
        {name: 'mdcConnected', mapping: 'mktDataConnection.connected'}
    ]
});