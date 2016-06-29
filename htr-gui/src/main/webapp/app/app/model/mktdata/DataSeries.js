/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('HtrGui.model.mktdata.DataSeries', {
    extend: 'HtrGui.model.mktdata.Base',

    fields: [
        {name: 'instrumentId', mapping: 'instrument.id'},
        {name: 'symbol', mapping: 'instrument.symbol'},
        {name: 'underlying', mapping: 'instrument.underlying'},
        {name: 'secType', mapping: 'instrument.secType'},
        {name: 'currency', mapping: 'instrument.currency'},
        {name: 'exchange', mapping: 'instrument.exchange'},
        'interval',
        'displayOrder',
        'active',
        'alias'
    ]
});