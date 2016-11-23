package com.android.stockhawk.quotes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.List;

/**
 * Created by sagar_000 on 11/12/2016.
 */

public class StockHistoryDataHandler {

    OkHttpClient mClient = new OkHttpClient();
    public String fetchStocks(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = mClient.newCall(request).execute();
            return response.body().string();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Quote> getStockQuotes(String url){
        String jsonData = fetchStocks(url);
        Gson gson = new GsonBuilder().create();
        QuotesResponse quotesResponse = gson.fromJson(jsonData, QuotesResponse.class);
        return quotesResponse.getQuotes();
    }

}
