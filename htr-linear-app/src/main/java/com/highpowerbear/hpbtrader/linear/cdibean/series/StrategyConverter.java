package com.highpowerbear.hpbtrader.linear.cdibean.series;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Created by rkolar on 6/2/14.
 */
@FacesConverter("strategyConverter")
public class StrategyConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        return Integer.parseInt(string);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (object instanceof Integer) {
            Integer strategyId = (Integer) object;
            return String.valueOf(strategyId);
        }
        return "";
    }
}