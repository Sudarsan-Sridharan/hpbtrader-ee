/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Exec.common.Definitions', {
    statics: {
        //urlPrefix: 'http://localhost:28080/htr-exec/rest',
        urlPrefix: 'http://' + window.location.host + '/htr-exec/rest',

        //wsUrl: 'ws://localhost:28080/htr-exec/websocket/exec'
        wsUrl: 'ws://' + window.location.host + '/htr-exec/websocket/exec'
    }
});