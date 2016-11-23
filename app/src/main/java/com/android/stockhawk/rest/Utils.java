package com.android.stockhawk.rest;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.util.Log;

import com.android.stockhawk.data.QuoteColumns;
import com.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sagar_000 on 10/15/2016.
 */
public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();

    public static boolean showPercent = true;

    public static String getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        return Constants.SIMPLE_DATE_FORMAT.format(calendar.getTime());
    }

    public static String getLastYear(){
        Calendar today = Calendar.getInstance();
        today.add(Calendar.MONTH,-12);
        return Constants.SIMPLE_DATE_FORMAT.format(today.getTime());
    }

    public static ArrayList quoteJsonToContentVals(String JSON){
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        try{
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0){
                jsonObject = jsonObject.getJSONObject("query");
                int count = Integer.parseInt(jsonObject.getString("count"));
                if (count == 1){
                    jsonObject = jsonObject.getJSONObject("results")
                            .getJSONObject("quote");
                    batchOperations.add(buildBatchOperation(jsonObject));
                } else{
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0){
                        for (int i = 0; i < resultsArray.length(); i++){
                            jsonObject = resultsArray.getJSONObject(i);
                            batchOperations.add(buildBatchOperation(jsonObject));
                        }
                    }
                }
            }
        } catch (JSONException e){
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    @SuppressLint("DefaultLocale")
    public static String truncateBidPrice(String bidPrice){
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange){
        String weight = change.substring(0,1);
        String ampersand = "";
        if (isPercentChange){
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuffer changeBuffer = new StringBuffer(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString("Change");
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    jsonObject.getString("ChangeinPercent"), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-'){
                builder.withValue(QuoteColumns.ISUP, 0);
            }else{
                builder.withValue(QuoteColumns.ISUP, 1);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
        return builder.build();
    }

    public static String getStockDataUrl(String stock_symbol){
        String startDate = Utils.getLastYear() ;
        String endDate = Utils.getTodayDate();
        try{
            String YAHOO_BASE_URL = Constants.YAHOO_BASE_QUERY;
            String QUERY_STOCK_DATA = Constants.QUERY_STOCK_DATA +
                    Constants.SYMBOL_QUERY +stock_symbol+ Constants.START_DATE_QUERY +startDate+"\" " +
                   Constants.END_DATE_QUERY + endDate+"\"";
            return YAHOO_BASE_URL + URLEncoder.encode(QUERY_STOCK_DATA, Constants.UTF)
                    + Constants.FORMAT_QUERY
                    + Constants.TABLES_CALLBACK_QUERY;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
