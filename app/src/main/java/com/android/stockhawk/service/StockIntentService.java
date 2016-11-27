package com.android.stockhawk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.stockhawk.R;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by sagar_000 on 10/15/2016.
 */
public class StockIntentService extends IntentService {

    private static final String LOG_TAG = StockIntentService.class.getSimpleName();

    public StockIntentService(){
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        final Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")){
            args.putString("symbol", intent.getStringExtra("symbol"));
            String sym = intent.getStringExtra("symbol");
            Log.d(LOG_TAG, "Search Symbol: " + sym);
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.)
       try{stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));}
       catch (Exception e){
           e.printStackTrace();
           Handler handler = new Handler(getMainLooper());
           handler.post(new Runnable() {
               @Override
               public void run() {
                   Context context = getApplicationContext();
                   Toast.makeText(context,getString(R.string.invalid_stock),Toast.LENGTH_SHORT).show();
               }
           });
       }
    }
}

