package com.highpowerbear.hpbtrader.options.model;

/**
 *
 * @author robertk
 */
public class ConstraintsStatus {
    private String optionSymbol;
    private boolean match = false;
    private String description;

    public ConstraintsStatus(String optionSymbol) {
        this.optionSymbol = optionSymbol;
    }

    public String getOptionSymbol() {
        return optionSymbol;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

   
}
