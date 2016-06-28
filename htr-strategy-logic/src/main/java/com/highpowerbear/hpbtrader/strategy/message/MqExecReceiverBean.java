package com.highpowerbear.hpbtrader.strategy.message;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.strategy.linear.OrderStateHandler;
import com.highpowerbear.hpbtrader.strategy.linear.ProcessContext;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyController;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 2.6.2016.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = HtrDefinitions.EXEC_TO_STRATEGY_QUEUE),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "5")
})
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MqExecReceiverBean implements MessageListener {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private IbOrderDao ibOrderDao;
    @Inject private StrategyController strategyController;
    @Inject private OrderStateHandler orderStateHandler;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String msg = ((TextMessage) message).getText();
                String corId = message.getJMSCorrelationID();
                l.info("Text message received from MQ="+ HtrDefinitions.EXEC_TO_STRATEGY_QUEUE + ", corId=" + corId + ", msg=" + msg);
                Long id = Long.valueOf(corId);
                IbOrder ibOrder = ibOrderDao.findIbOrder(id);
                HtrEnums.MessageType messageType = HtrUtil.parseMessageType(msg);
                if (HtrEnums.MessageType.ORDER_STATUS_CHANGED.equals(messageType)) {
                    ProcessContext ctx = strategyController.getTradingContext(ibOrder.getStrategy());
                    orderStateHandler.orderStateChanged(ctx, ibOrder);
                }
            } else {
                l.warning("Non-text message received from MQ=" + HtrDefinitions.EXEC_TO_STRATEGY_QUEUE + ", ignoring");
            }
        } catch (JMSException e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}
