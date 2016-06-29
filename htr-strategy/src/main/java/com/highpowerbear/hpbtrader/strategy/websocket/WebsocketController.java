package com.highpowerbear.hpbtrader.strategy.websocket;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 5/17/14.
 */
@ApplicationScoped
public class WebsocketController implements Serializable {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

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

    public void broadcastMessage(String message) {
        //l.l().debug("Sending websocket message=" + message + ", clients=" + sessions.size());
        sessions.stream().filter(Session::isOpen).forEach(s -> sendMessage(s, message));
    }
}