package com.android.stockhawk.quotes;

import java.util.List;

/**
 * Created by sagar_000 on 11/10/2016.
 */

public class QuotesResponse {

        public QuotesResponse() {
        }

        Query query;
        public List<Quote> getQuotes(){
            return this.query.results.quote;
        }

    }

