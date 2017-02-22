package com.yarolegovich.currencyexchange.net;

/**
 * Created by yarolegovich on 22.02.2017.
 */

public class HttpError extends Exception {
    public HttpError(String message) {
        super(message);
    }
}
