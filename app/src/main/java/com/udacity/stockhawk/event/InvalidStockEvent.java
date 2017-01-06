package com.udacity.stockhawk.event;

/**
 * Created by sai on 1/5/17.
 */

public class InvalidStockEvent {
    public final String symbol;

    public InvalidStockEvent(String symbol) {
        this.symbol = symbol;
    }
}
