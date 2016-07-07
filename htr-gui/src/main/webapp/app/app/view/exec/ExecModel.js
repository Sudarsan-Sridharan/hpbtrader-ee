/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.exec.ExecModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'HtrGui.model.exec.IbAccount',
        'HtrGui.model.exec.IbOrder'
    ],

    alias: 'viewmodel.htr-exec',

    stores: {
        ibAccounts: {
            model: 'HtrGui.model.exec.IbAccount',
            pageSize: 10
        },
        ibOrders: {
            model: 'HtrGui.model.exec.IbOrder',
            pageSize: 10
        }
    }
});