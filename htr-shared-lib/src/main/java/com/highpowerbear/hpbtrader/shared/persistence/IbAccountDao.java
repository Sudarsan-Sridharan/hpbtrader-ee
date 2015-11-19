package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.IbAccount;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface IbAccountDao {
    IbAccount findIbAccount(String accountId);
    List<IbAccount> getIbAccounts();
    IbAccount updateIbAccount(IbAccount ibAccount);
}
