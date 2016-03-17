package com.combah.currencyalert.network;

import com.combah.currencyalert.model.Currency;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rodrigo on 3/17/16.
 */
public class YahooResponse {

    @SerializedName("list")
    public ResponseList list;

    public class ResponseList {
        @SerializedName("resources")
        public List<ResourceWrapper> resources;
    }

    public class ResourceWrapper {

        @SerializedName("resource")
        public Resource resource;

        public class Resource {
            @SerializedName("fields")
            public Currency currency;
        }

    }
}
