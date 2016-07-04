/**
 * Created by robertk on 9/6/15.
 */
Ext.define('HtrGui.common.Definitions', {
    statics: {
        //urlPrefix: 'http://localhost:28080/htr-mktdata/rest',
        urlPrefixMktData: 'http://' + window.location.host + '/htr-mktdata/rest',

        //wsUrl: 'ws://localhost:28080/htr-mktdata/websocket/mktdata'
        wsUrlMktData: 'ws://' + window.location.host + '/htr-mktdata/websocket/mktdata'
    }
});