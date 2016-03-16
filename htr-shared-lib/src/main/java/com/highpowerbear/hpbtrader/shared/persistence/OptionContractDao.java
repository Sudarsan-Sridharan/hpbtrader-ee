package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.OptionContract;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Created by robertk on 24.12.2015.
 */
public interface OptionContractDao {
    void createOptionContract(OptionContract optionContract);
    String getCallSymbol(String underlying, Calendar minExpiry, Double maxStrike);
    String getPutSymbol(String underlying, Calendar minExpiry, Double minStrike);
    List<OptionContract> getOptionContracts(Set<String> underlyings);
}
