/**
 * Created by robertk on 9/6/15.
 */
Ext.define('HtrGui.common.Definitions', {
    statics: {
        //urlPrefixMktData: 'http://localhost:28080/htr-mktdata/rest',
        urlPrefixMktData: 'http://' + window.location.host + '/htr-mktdata/rest',

        //wsUrlMktData: 'ws://localhost:28080/htr-mktdata/websocket',
        wsUrlMktData: 'ws://' + window.location.host + '/htr-mktdata/websocket',

        //urlPrefixExec: 'http://localhost:28080/htr-exec/rest',
        urlPrefixExec: 'http://' + window.location.host + '/htr-exec/rest',

        //wsUrlExec: 'ws://localhost:28080/htr-exec/websocket',
        wsUrlExec: 'ws://' + window.location.host + '/htr-exec/websocket',

        //urlPrefixStrategy: 'http://localhost:28080/htr-strategy/rest',
        urlPrefixStrategy: 'http://' + window.location.host + '/htr-strategy/rest',

        //wsUrlStrategy: 'ws://localhost:28080/htr-strategy/websocket'
        wsUrlStrategy: 'ws://' + window.location.host + '/htr-strategy/websocket'
    }
});