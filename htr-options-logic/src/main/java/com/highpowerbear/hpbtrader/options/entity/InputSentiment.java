package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author rkolar
 */
@Entity
@Table(name = "opt_inputsentiment")
public class InputSentiment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="opt_inputsentiment")
    @Id
    @GeneratedValue(generator="opt_inputsentiment")
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar sentimentDate;
    @Enumerated(EnumType.STRING)
    private OptEnums.SentimentOrigin origin;
    @Enumerated(EnumType.STRING)
    private OptEnums.Underlying underlying;
    @Enumerated(EnumType.STRING)
    private OptEnums.SentimentType sentimentType;
    private String sentimentDesc;
    private Boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputSentiment that = (InputSentiment) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getSentimentDate() {
        return sentimentDate;
    }

    public void setSentimentDate(Calendar sentimentDate) {
        this.sentimentDate = sentimentDate;
    }

    public OptEnums.SentimentOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(OptEnums.SentimentOrigin origin) {
        this.origin = origin;
    }

    public OptEnums.Underlying getUnderlying() {
        return underlying;
    }

    public void setUnderlying(OptEnums.Underlying underlying) {
        this.underlying = underlying;
    }

    public OptEnums.SentimentType getSentimentType() {
        return sentimentType;
    }

    public void setSentimentType(OptEnums.SentimentType sentimentType) {
        this.sentimentType = sentimentType;
    }

    public String getSentimentDesc() {
        return sentimentDesc;
    }

    public void setSentimentDesc(String sentimentDesc) {
        this.sentimentDesc = sentimentDesc;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
