package com.highpowerbear.hpbtrader.linear.websocket;

import com.highpowerbear.hpbtrader.linear.common.LinSettings;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.websocket.Session;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 5/17/14.
 */
@Named
@ApplicationScoped
public class WebsocketController implements Serializable {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);

    private Set<Session> sessions = new HashSet<>();

    public Set<Session> getSessions() {
        return sessions;
    }

    public void sendMessage(Session s, String message) {
        try {
            s.getBasicRemote().sendText(message);
        } catch (Throwable ioe) {
            l.log(Level.SEVERE, "Error sending websocket message " + message, ioe);
        }
    }

    public void broadcastSeriesMessage(String message) {
        //l.l().debug("Sending websocket message=" + message + ", clients=" + sessions.size());
        sessions.stream().filter(Session::isOpen).forEach(s -> sendMessage(s, message));
    }
}