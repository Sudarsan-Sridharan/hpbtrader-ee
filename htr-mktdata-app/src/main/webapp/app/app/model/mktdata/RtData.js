/**
 * Created by robertk on 6.4.2016.
 */
Ext.define('MktData.model.mktdata.RtData', {
    extend: 'MktData.model.mktdata.Base',

    idProperty: 'dataSeriesId',

    fields: [
        {name: 'symbol', mapping: 'instrument.symbol'},
        {name: 'underlying', mapping: 'instrument.underlying'},
        {name: 'secType', mapping: 'instrument.secType'},
        {name: 'currency', mapping: 'instrument.currency'},
        {name: 'exchange', mapping: 'instrument.exchange'},
        
        {name: 'bidValue', mapping: 'bid.value'},
        {name: 'bidStatus', mapping: 'bid.status'},
        {name: 'bidFieldName', mapping: 'bid.fieldName'},

        {name: 'askValue', mapping: 'ask.value'},
        {name: 'askStatus', mapping: 'ask.status'},
        {name: 'askFieldName', mapping: 'ask.fieldName'},

        {name: 'lastValue', mapping: 'last.value'},
        {name: 'lastStatus', mapping: 'last.status'},
        {name: 'lastFieldName', mapping: 'last.fieldName'},

        {name: 'closeValue', mapping: 'close.value'},
        {name: 'closeStatus', mapping: 'close.status'},
        {name: 'closeFieldName', mapping: 'close.fieldName'},

        'changePctStr',
        {name: 'changePctValue', mapping: 'changePct.value'},
        {name: 'changePctStatus', mapping: 'changePct.status'},
        {name: 'changePctFieldName', mapping: 'changePct.fieldName'},

        {name: 'bidSizeValue', mapping: 'bidSize.value'},
        {name: 'bidSizeStatus', mapping: 'bidSize.status'},
        {name: 'bidSizeFieldName', mapping: 'bidSize.fieldName'},

        {name: 'askSizeValue', mapping: 'askSize.value'},
        {name: 'askSizeStatus', mapping: 'askSize.status'},
        {name: 'askSizeFieldName', mapping: 'askSize.fieldName'},

        {name: 'lastSizeValue', mapping: 'lastSize.value'},
        {name: 'lastSizeStatus', mapping: 'lastSize.status'},
        {name: 'lastSizeFieldName', mapping: 'lastSize.fieldName'},

        {name: 'volumeValue', mapping: 'volume.value'},
        {name: 'volumeStatus', mapping: 'volume.status'},
        {name: 'volumeFieldName', mapping: 'volume.fieldName'}
    ]
});