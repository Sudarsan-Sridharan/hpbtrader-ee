/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Strategy.common.Definitions', {
    statics: {
        //urlPrefix: 'http://localhost:28080/htr-strategy/rest',
        urlPrefix: 'http://' + window.location.host + '/htr-strategy/rest',

        //wsUrl: 'ws://localhost:28080/htr-strategy/websocket/strategy'
        wsUrl: 'ws://' + window.location.host + '/htr-strategy/websocket/strategy'
    }
});