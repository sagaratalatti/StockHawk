package com.android.stockhawk.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by sagar_000 on 10/15/2016.
 */
@Database(version = QuoteDatabase.VERSION)
public class QuoteDatabase {
    private QuoteDatabase(){}

    public static final int VERSION = 7;

    @Table(QuoteColumns.class) public static final String QUOTES = "quotes";
}
