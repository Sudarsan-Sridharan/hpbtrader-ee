package com.highpowerbear.hpbtrader.exec.message;

import com.highpowerbear.hpbtrader.exec.ibclient.IbController;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 18.5.2016.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/StrategyToExecQ"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MqReceiverBean implements MessageListener {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private IbOrderDao ibOrderDao;
    @Inject private IbController ibController;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String msg = ((TextMessage) message).getText();
                String corId = message.getJMSCorrelationID();
                l.info("Text message received from MQ=StrategyToExecQ, corId=" + corId + ", msg=" + msg);
                Long id = Long.valueOf(corId);
                IbOrder ibOrder = ibOrderDao.findIbOrder(id);
                HtrEnums.MessageType messageType = HtrUtil.parseMessageType(msg);
                if (HtrEnums.MessageType.NEW_ORDER.equals(messageType)) {
                    ibController.submitIbOrder(ibOrder);
                }
            } else {
                l.warning("Non-text message received from MQ=StrategyToExecQ, ignoring");
            }
        } catch (JMSException e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}