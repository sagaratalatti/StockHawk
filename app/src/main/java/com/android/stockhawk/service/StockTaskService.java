package com.android.stockhawk.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.util.Log;

import com.android.stockhawk.data.QuoteColumns;
import com.android.stockhawk.data.QuoteProvider;
import com.android.stockhawk.quotes.Quote;
import com.android.stockhawk.quotes.RealmController;
import com.android.stockhawk.quotes.SaveStocks;
import com.android.stockhawk.quotes.StockHistoryDataHandler;
import com.android.stockhawk.rest.Constants;
import com.android.stockhawk.rest.Utils;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import io.realm.Realm;

/**
 * Created by sagar_000 on 10/15/2016.
 */
public class StockTaskService extends GcmTaskService {



    private String LOG_TAG = StockTaskService.class.getSimpleName();

    private OkHttpClient client = new OkHttpClient();
    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean isUpdate;

    public StockTaskService(){}

    public StockTaskService(Context context){
        mContext = context;
    }
    String fetchData(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public int onRunTask(TaskParams params){
        // default value
        String stock_symbol = Constants.DEFAULT_STOCK_SYMBOL;
        if(params!=null && params.getExtras()!=null)
            if(params.getExtras().containsKey(Constants.STOCK_KEY))
                stock_symbol = params.getExtras().getString(Constants.STOCK_KEY);
        Cursor initQueryCursor;
        if (mContext == null){
            mContext = this;
        }
        StringBuilder urlStringBuilder = new StringBuilder();
        try{
            // Base URL for the Yahoo query
            urlStringBuilder.append(Constants.YAHOO_BASE_QUERY);
            urlStringBuilder.append(URLEncoder.encode(Constants.YAHOO_QUERY_SYMBOL
                    + "in (", Constants.UTF));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (params.getTag().equals(Constants.INIT) || params.getTag().equals(Constants.PERIODIC)){
            isUpdate = true;
            initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[] { Constants.DISTINCT + QuoteColumns.SYMBOL }, null,
                    null, null);
            if (initQueryCursor.getCount() == 0 || initQueryCursor == null){
                // Init task. Populates DB with quotes for the symbols seen below
                try {
                    urlStringBuilder.append(
                            URLEncoder.encode("\""+stock_symbol+"\")", Constants.UTF));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (initQueryCursor != null){
                DatabaseUtils.dumpCursor(initQueryCursor);
                initQueryCursor.moveToFirst();
                for (int i = 0; i < initQueryCursor.getCount(); i++){
                    mStoredSymbols.append("\""+
                            initQueryCursor.getString(initQueryCursor.getColumnIndex(Constants.STOCK_KEY))+"\",");
                    initQueryCursor.moveToNext();
                }
                mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
                try {
                    urlStringBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), Constants.UTF));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (params.getTag().equals(Constants.ADD)){
            isUpdate = false;
            // get symbol from params.getExtra and build query

            try {
                urlStringBuilder.append(URLEncoder.encode("\""+stock_symbol+"\")", Constants.UTF));
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
        // finalize the URL for the API query.
        urlStringBuilder.append( Constants.DATA_TABLES
                + Constants.TABLES_CALLBACK_QUERY);

        String urlString;
        String getResponse;
        int result = GcmNetworkManager.RESULT_FAILURE;

        if (urlStringBuilder != null){
            urlString = urlStringBuilder.toString();
            try{
                // get current stock info
                getResponse = fetchData(urlString);

                getStockQuotes(stock_symbol);

                result = GcmNetworkManager.RESULT_SUCCESS;
                try {
                    ContentValues contentValues = new ContentValues();
                    if (isUpdate){
                        contentValues.put(QuoteColumns.ISCURRENT, 0);
                        mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                                null, null);
                    }
                    mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                            Utils.quoteJsonToContentVals(getResponse));
                    // send broadcast so Widget can Update data
                    Intent broadcastIntent = new Intent(Constants.STOCK_UPDATE)
                            .setPackage(mContext.getPackageName());
                    mContext.sendBroadcast(broadcastIntent);

                }catch (RemoteException | OperationApplicationException e){
                    Log.e(LOG_TAG, "Error applying batch insert", e);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return result;
    }

    public void getStockQuotes(String stockSymbol){
        try {
            String url = Utils.getStockDataUrl(stockSymbol);
            Realm.init(mContext);
            List<Quote> quotes = new StockHistoryDataHandler().getStockQuotes(url);
            SaveStocks saveStocks = new SaveStocks(stockSymbol, quotes);
            io.realm.Realm realm = new RealmController(getApplication()).getRealm();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(saveStocks);
            realm.commitTransaction();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}

