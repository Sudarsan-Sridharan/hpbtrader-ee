package com.highpowerbear.hpbtrader.strategy.message;

/**
 * Created by robertk on 11/26/2015.
 */

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyController;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/MktDataToStrategyQ"),
@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MqListenerBean implements MessageListener {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private StrategyController strategyController;
    @Inject private StrategyDao strategyDao;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String seriesAlias = ((TextMessage) message).getText();
                l.info("Text message received from MQ=MktDataToStrategyQ, corId=" + message.getJMSCorrelationID() + ", seriesAlias=" + seriesAlias);
                List<Strategy> strategies = strategyDao.getStrategies(seriesAlias);
                strategies.forEach(str -> strategyController.queueProcessStrategy(str, seriesAlias));
            } else {
                l.warning("Non-text message received from MQ=MktDataToStrategyQ, ignoring");
            }
        } catch (JMSException e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}
