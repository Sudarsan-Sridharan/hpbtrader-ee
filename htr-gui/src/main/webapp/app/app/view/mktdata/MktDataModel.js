/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('HtrGui.view.mktdata.MktDataModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'HtrGui.model.mktdata.DataBar',
        'HtrGui.model.mktdata.DataSeries',
        'HtrGui.model.mktdata.IbAccount',
        'HtrGui.model.mktdata.RtData',
        'HtrGui.model.indicator.Ema',
        'HtrGui.model.indicator.Macd',
        'HtrGui.model.indicator.Stochastics'
    ],

    alias: 'viewmodel.htr-mktdata',

    stores: {
        ibAccounts: {
            model: 'HtrGui.model.mktdata.IbAccount',
            pageSize: 10
        },
        dataSeriesStore: {
            model: 'HtrGui.model.mktdata.DataSeries',
            pageSize: 10
        },
        dataBars: {
            model: 'HtrGui.model.mktdata.DataBar',
            pageSize: 10
        },
        rtDataStore: {
            model: 'HtrGui.model.mktdata.RtData',
            pageSize: 10
        }
    }
});