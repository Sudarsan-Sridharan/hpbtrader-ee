package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * Created by robertk on 11/14/2015.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name="ibaccount")
public class IbAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String accountId;
    private String host;
    private Integer port;
    private Integer clientId;

    @Transient
    private IbConnection ibConnection;

    public String print() {
        return accountId + ", " + host + ":" + port + ", " + clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IbAccount ibAccount = (IbAccount) o;

        return !(accountId != null ? !accountId.equals(ibAccount.accountId) : ibAccount.accountId != null);
    }

    @Override
    public int hashCode() {
        return accountId != null ? accountId.hashCode() : 0;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public IbConnection getIbConnection() {
        return ibConnection;
    }

    public void setIbConnection(IbConnection ibConnection) {
        this.ibConnection = ibConnection;
    }
}
