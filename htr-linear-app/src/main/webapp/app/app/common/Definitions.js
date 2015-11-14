/**
 * Created by robertk on 11/14/15.
 */
Ext.define('Linear.common.Definitions', {
    statics: {
        //urlPrefix: 'http://localhost:28080/han-linear/rest/linear',
        urlPrefix: 'http://' + window.location.host + '/han-linear/rest/linear',

        //wsUrl: 'ws://localhost:28080/han-linear/websocket/linear'
        wsUrl: 'ws://' + window.location.host + '/han-linear/websocket/linear'
    }
});