package com.highpowerbear.hpbtrader.mktdata.message;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import java.util.logging.Logger;

/**
 * Created by robertk on 25.11.2015.
 */
@Stateless
public class MqSender {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private JMSContext jmsContext;
    @Resource(lookup = "java:/jms/queue/mktdata_strategy")
    private Queue mktDataToStrategyQ;

    public void notifyBarsAdded(DataSeries dataSeries) {
        l.info("BEGIN send message to MQ=mktdata_strategy, dataSeries=" + dataSeries.getId() + " (" + dataSeries.getAlias() + ")");
        JMSProducer producer = jmsContext.createProducer();
        TextMessage message = jmsContext.createTextMessage(dataSeries.getId() + "," + dataSeries.getAlias());
        producer.send(mktDataToStrategyQ, message);
        l.info("END send message to MQ=mktdata_strategy, dataSeries=" + dataSeries.getId() + " (" + dataSeries.getAlias() + ")");
    }
}
