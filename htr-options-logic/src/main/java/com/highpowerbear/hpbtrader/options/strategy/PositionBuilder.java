package com.highpowerbear.hpbtrader.options.strategy;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.model.PositionLeg;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertk on 29.9.2015.
 */
@Named
@ApplicationScoped
public class PositionBuilder {

    public List<PositionLeg> buildFull(OptEnums.Underlying underlying, Double delta, Double otmPoints, Double timeValuePct) {
        List<PositionLeg> legs = new ArrayList<>();
        // TODO full builder logic
        return legs;
    }

    public List<PositionLeg> buildPartial(OptEnums.Underlying underlying, Double delta, Double otmPoints, Double timeValuePct, List<PositionLeg> existingLegs) {
        List<PositionLeg> legs = new ArrayList<>();
        // TODO partial builder logic
        return legs;
    }
}
