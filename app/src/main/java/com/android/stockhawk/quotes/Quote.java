package com.android.stockhawk.quotes;

import com.google.gson.annotations.Expose;

import net.simonvt.schematic.annotation.PrimaryKey;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by sagar_000 on 11/9/2016.
 */

public class Quote extends RealmObject {

    public String Symbol;

    public String Date;

    public String Close;

    @Expose(serialize = false, deserialize = false)
    Date realDate;

    public Quote(){}

    public Quote(String Symbol ,Date realDate, String Close){
        this.Symbol = Symbol;
        this.realDate = realDate;
        this.Close = Close;
    }

    public String getSymbol(){
        return Symbol;
    }

    public String getDate(){
        return Date;
    }

    public String getClose(){
        return Close;
    }

    public void setClose(String Close){
        this.Close = Close;
    }
    public void setRealDate (Date realDate){
        this.realDate = realDate;
    }

    public Date getRealDate(){
        return realDate;
    }


}
