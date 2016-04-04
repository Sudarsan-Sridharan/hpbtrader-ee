/**
 * Created by robertk on 4/3/2016.
 */
Ext.define('MktData.model.mktdata.DataBar', {
    extend: 'MktData.model.mktdata.Base',

    fields: [
        'dataSeriesId',
        {name: 'barCloseDate', type: 'date', dateFormat: 'time'},
        'barOpen',
        'barHigh',
        'barHigh',
        'barClose',
        'volume',
        'count',
        'wap',
        'hasGaps'
    ]
});