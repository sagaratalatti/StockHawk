package com.android.stockhawk.rest;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by sagar_000 on 11/2/2016.
 */
public class Constants {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static final String YAHOO_BASE_QUERY = "http://query.yahooapis.com/v1/public/yql?q=";
    public static final String INIT = "init";
    public static final String TAG = "tag";
    public static final String PERIODIC = "periodic";
    public static final String YAHOO_QUERY_SYMBOL = "select * from yahoo.finance.quotes where symbol ";
    public static final String QUERY_STOCK_DATA = "select * from yahoo.finance.historicaldata where ";
    public static final String SYMBOL_QUERY = "symbol = \"";
    public static final String START_DATE_QUERY = "\" and startDate = \"";
    public static final String END_DATE_QUERY =  "and endDate = \"";
    public static final String FORMAT_QUERY = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.";
    public static final String TABLES_CALLBACK_QUERY = "org%2Falltableswithkeys&callback=";
    public static final String DEFAULT_STOCK_SYMBOL = "YHOO";
    public static final String STOCK_KEY = "symbol";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATA_TABLES = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.";
    public static final String STOCK_UPDATE = "com.android.stockhawk.STOCK_UPDATE";
    public static final String ADD = "add";
    public static final String UTF = "UTF-8";
    public static final String DISTINCT = "Distinct ";
}
