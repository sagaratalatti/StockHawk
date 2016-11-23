package com.android.stockhawk.quotes;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import io.realm.Realm;


/**
 * Created by sagar on 18-11-2016.
 */

public class RealmController {

    public static final String STOCK_SYMBOL = "stock_symbol";

    private static RealmController instance;


    private final Realm realm;

    public RealmController(Application application){
        realm = Realm.getDefaultInstance();
    }


    public static RealmController with(Fragment fragment) {
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }



    public static RealmController with(Activity activity) {
        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }



    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }



    public static RealmController getInstance() {
        return instance;
    }


    public Realm getRealm() {
        return realm;
    }

    public SaveStocks getStockData(String stock_symbol){
        return realm.where(SaveStocks.class).equalTo(STOCK_SYMBOL,stock_symbol).findFirst();
    }


    public boolean checkIfStockSaved(String stock_symbol){

        return getStockData(stock_symbol)!=null;
    }



    public void deleteStockGraphData(String stock_symbol){

        realm.beginTransaction();
        if (checkIfStockSaved(stock_symbol)){
            SaveStocks saveStocks = getStockData(stock_symbol);
            saveStocks.deleteFromRealm();
        }
        realm.commitTransaction();
    }

}
