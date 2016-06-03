package com.highpowerbear.hpbtrader.mktdata.message;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 25.11.2015.
 */
@Named
@ApplicationScoped
public class MqSender {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private JMSContext jmsContext;
    @Resource(lookup = "java:/jms/queue/MktDataToStrategyQ")
    private Queue mktDataToStrategyQ;

    public void notifyBarsAdded(DataSeries dataSeries) {
        try {
            String corId = String.valueOf(dataSeries.getId());
            String msg = HtrUtil.constructMessage(HtrEnums.MessageType.BARS_ADDED, dataSeries.getAlias());
            l.info("BEGIN send message to MQ=MktDataToStrategyQ, corId=" + corId + ", msg=" + msg);
            JMSProducer producer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage(msg);
            message.setJMSCorrelationID(corId);
            producer.send(mktDataToStrategyQ, message);
            l.info("END send message to MQ=MktDataToStrategyQ, corId=" + corId + ", msg=" + msg);
        } catch (JMSException e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}
