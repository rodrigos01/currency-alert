package com.combah.currencyalert.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rodrigo on 3/17/16.
 */
public class Currency {

    @SerializedName("symbol")
    public String symbol;

    @SerializedName("price")
    public double value;

}
