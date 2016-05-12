package com.highpowerbear.hpbtrader.shared.model;

/**
 * Created by robertk on 4.3.2016.
 */
public class OperResult<S, C> {
    private S status;
    private C content;

    public OperResult(S status, C content) {
        this.status = status;
        this.content = content;
    }

    public S getStatus() {
        return status;
    }

    public C getContent() {
        return this.content;
    }
}