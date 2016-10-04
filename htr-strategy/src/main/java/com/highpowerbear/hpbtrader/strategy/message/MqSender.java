package com.highpowerbear.hpbtrader.strategy.message;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 18.5.2016.
 */
// needs to be stateless since it is invoked from managed executor thread, the following is reported if using @ApplicationScoped
// Error: org.jboss.weld.context.ContextNotActiveException: WELD-001303: No active contexts for scope type javax.enterprise.context.RequestScoped
@Stateless
public class MqSender {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private JMSContext jmsContext;
    @Resource(lookup = HtrDefinitions.STRATEGY_TO_EXEC_QUEUE)
    private Queue strategyToExecQ;

    public void notifyIbOrderCreated(IbOrder ibOrder) {
        try {
            String corId = String.valueOf(ibOrder.getId());
            String msg = HtrUtil.constructMessage(HtrEnums.MessageType.IBORDER_CREATED, ibOrder.getDescription());
            l.info("BEGIN send message to MQ=" + HtrDefinitions.STRATEGY_TO_EXEC_QUEUE + ", corId=" + corId + ", msg=" + msg);
            JMSProducer producer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage(msg);
            message.setJMSCorrelationID(corId);
            producer.send(strategyToExecQ, message);
            l.info("END send message to MQ=" + HtrDefinitions.STRATEGY_TO_EXEC_QUEUE + ", corId=" + ibOrder.getId() + ", " + ibOrder.getDescription());
        } catch (JMSException e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}