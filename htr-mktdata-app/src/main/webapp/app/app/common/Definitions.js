/**
 * Created by robertk on 9/6/15.
 */
Ext.define('MktData.common.Definitions', {
    statics: {
        //urlPrefix: 'http://localhost:28080/htr-mktdata/rest',
        urlPrefix: 'http://' + window.location.host + '/htr-mktdata/rest',

        //wsUrl: 'ws://localhost:28080/htr-mktdata/websocket/mktdata'
        wsUrl: 'ws://' + window.location.host + '/htr-mktdata/websocket/mktdata'
    }
});