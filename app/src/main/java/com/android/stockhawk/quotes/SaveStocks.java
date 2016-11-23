package com.android.stockhawk.quotes;

import android.util.Log;

import com.android.stockhawk.rest.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sagar on 18-11-2016.
 */


public class SaveStocks extends RealmObject {

    @PrimaryKey
    String stock_symbol;

    private RealmList<Quote> stockList = new RealmList<>();

    public SaveStocks(){}

    public SaveStocks(String stock_symbol, List<Quote> quoteList) {

        try {
            this.stock_symbol = stock_symbol;
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);

            for (Quote quote: quoteList ) {
                Date date = dateFormat.parse(quote.getDate());
                quote.setRealDate(date);
                quote.setClose(quote.getClose());
                this.stockList.add(quote);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public int size(){
        return stockList.size();
    }



    public RealmList<Quote> getStockList() {
        return stockList;
    }
}
