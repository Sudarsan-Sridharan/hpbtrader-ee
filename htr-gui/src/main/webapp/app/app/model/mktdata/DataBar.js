/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('HtrGui.model.mktdata.DataBar', {
    extend: 'HtrGui.model.mktdata.Base',

    idProperty: 'barCloseDate',

    fields: [
        {name: 'barCloseDate', type: 'date', dateFormat: 'time'},
        'dataSeriesId',
        'barOpen',
        'barHigh',
        'barLow',
        'barClose',
        'volume',
        'count',
        'wap',
        'hasGaps'
    ]
});