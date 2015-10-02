package com.highpowerbear.hpbtrader.options.execution.action;

import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.execution.GenericAction;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
@Named
@ApplicationScoped
public class OpenShort extends GenericAction {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Override
    public Long process(String underlying, String name) {
        // TODO process
        return null;
    }
}
