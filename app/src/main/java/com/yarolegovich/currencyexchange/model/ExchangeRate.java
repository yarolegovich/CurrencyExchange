package com.yarolegovich.currencyexchange.model;

import java.util.Date;

/**
 * Created by yarolegovich on 22.02.2017.
 */

public class ExchangeRate {

    private final Date date;
    private final double closeRate;
    private final double openRate;
    private final double lowRate;
    private final double highRate;

    public ExchangeRate(Date date, double openRate, double closeRate, double lowRate, double highRate) {
        this.date = date;
        this.openRate = openRate;
        this.closeRate = closeRate;
        this.lowRate = lowRate;
        this.highRate = highRate;
    }

    public Date getDate() {
        return (Date) date.clone();
    }

    public double getCloseRate() {
        return closeRate;
    }

    public double getOpenRate() {
        return openRate;
    }

    public double getDiff() {
        return closeRate - openRate;
    }

    public boolean isGrowing() {
        return getDiff() > 0;
    }

    public double getLowRate() {
        return lowRate;
    }

    public double getHighRate() {
        return highRate;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "date=" + date +
                ", rate=" + closeRate +
                '}';
    }
}
