/**
 * Created by robertk on 9/6/15.
 */
Ext.define('HtrGui.common.Definitions', {
    statics: {
        //urlPrefixMktData: 'http://localhost:28080/htr-mktdata/rest',
        urlPrefixMktData: 'http://' + window.location.host + '/htr-mktdata/rest',

        //wsUrlMktData: 'ws://localhost:28080/htr-mktdata/websocket/mktdata'
        wsUrlMktData: 'ws://' + window.location.host + '/htr-mktdata/websocket/mktdata',

        //urlPrefixExec: 'http://localhost:28080/htr-exec/rest',
        urlPrefixExec: 'http://' + window.location.host + '/htr-exec/rest',

        //wsUrlExec: 'ws://localhost:28080/htr-exec/websocket/exec'
        wsUrlExec: 'ws://' + window.location.host + '/htr-exec/websocket/exec'
    },

    getIbOrderStatusColor: function(status) {
        var statusColor;

        switch(status) {
            case 'SUBMITTED':   statusColor = 'blue';   break;
            case 'UPDATED':     statusColor = 'blue';   break;
            case 'CANCELLED':   statusColor = 'brown';  break;
            case 'FILLED':      statusColor = 'green';  break;
            case 'UNKNOWN':     statusColor = 'gray';   break;
        }
        return statusColor;
    }
});