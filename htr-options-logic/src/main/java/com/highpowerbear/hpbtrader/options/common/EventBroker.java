package com.highpowerbear.hpbtrader.options.common;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;

/**
 *
 * @author rkolar
 */
@Named
@Singleton
public class EventBroker {
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void trigger(final OptEnums.DataChangeEvent dataChangeEvent) {
    }
}