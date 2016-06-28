package com.highpowerbear.hpbtrader.exec.message;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 18.5.2016.
 */
@ApplicationScoped
public class MqSender {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private JMSContext jmsContext;
    @Resource(lookup = HtrDefinitions.EXEC_TO_STRATEGY_QUEUE)
    private Queue execToStrategyQ;

    public void notifyOrderStateChanged(IbOrder ibOrder) {
        try {
            String corId = String.valueOf(ibOrder.getId());
            String msg = HtrUtil.constructMessage(HtrEnums.MessageType.ORDER_STATUS_CHANGED, ibOrder.getDescription() + ", status=" + ibOrder.getStatus());
            l.info("BEGIN send message to MQ=" + HtrDefinitions.EXEC_TO_STRATEGY_QUEUE + ", corId=" + corId + ", msg=" + msg);
            JMSProducer producer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage(msg);
            message.setJMSCorrelationID(corId);
            producer.send(execToStrategyQ, message);
            l.info("END send message to MQ=" + HtrDefinitions.EXEC_TO_STRATEGY_QUEUE + ", corId=" + corId + ", msg=" + msg);
        } catch (JMSException e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}
