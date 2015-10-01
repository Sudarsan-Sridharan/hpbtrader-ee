package com.highpowerbear.hpbtrader.options.execution.action;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.entity.OptionOrder;
import com.highpowerbear.hpbtrader.options.entity.InputSentiment;
import com.highpowerbear.hpbtrader.options.entity.Trade;
import com.highpowerbear.hpbtrader.options.model.ConstraintsStatus;
import com.highpowerbear.hpbtrader.options.model.ContractProperties;
import com.highpowerbear.hpbtrader.options.model.ReadinessStatus;
import com.highpowerbear.hpbtrader.options.execution.GenericAction;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
@Named
@ApplicationScoped
public class Reverse extends GenericAction {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Override
    public Long process(String underlying, String name) {
        // TODO process
        return null;
    }
}
