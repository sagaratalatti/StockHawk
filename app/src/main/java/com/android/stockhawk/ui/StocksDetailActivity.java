package com.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.android.stockhawk.R;
import com.android.stockhawk.data.QuoteColumns;
import com.android.stockhawk.data.QuoteProvider;
import com.android.stockhawk.quotes.Quote;
import com.android.stockhawk.quotes.RealmController;
import com.android.stockhawk.quotes.SaveStocks;
import com.android.stockhawk.quotes.StockHistoryDataHandler;
import com.android.stockhawk.rest.Constants;
import com.android.stockhawk.rest.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;

import static java.security.AccessController.getContext;

/**
 * Created by sagar_000 on 10/23/2016.
 */
public class StocksDetailActivity extends AppCompatActivity {

    LineChart mChart;
    LineDataSet Set = new LineDataSet(new ArrayList<Entry>(),"Values");
    LineData mLineData;
    Realm mRealm;
    RealmController mRealmController;
    String stockSymbol;
    Cursor mCursor;
    TextView stock_details;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);
        mRealmController = RealmController.with(this);
        mRealm = mRealmController.getRealm();
        Set.clear();
        setContentView(R.layout.activity_line_graph);
        Intent intent = getIntent();
        if (intent!=null)
            stockSymbol = intent.getStringExtra(Constants.STOCK_KEY);
        mCursor = getContentResolver().query(
                QuoteProvider.Quotes.withSymbol(stockSymbol),
                null,
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null
        );
        String bid_price = "0";
        try{
            mCursor.moveToFirst();
            bid_price = "$"+mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE));
        }catch (Exception e){
            e.printStackTrace();
        }
        stock_details = (TextView)findViewById(R.id.stock_details);
        mChart = (LineChart)findViewById(R.id.stock_line_chart);
        mChart.setDrawGridBackground(true);
        stock_details.setText(bid_price);

        if (mRealmController.checkIfStockSaved(stockSymbol)) {
            try {
                designGraph();
                Calendar startMonth = Calendar.getInstance();
                startMonth.add(Calendar.MONTH,-12);
                populateData(startMonth.getTime());
                CustomMarkerView markerView = new CustomMarkerView(this, R.layout.marker_view_layout);
                mChart.setMarkerView(markerView);
                mChart.invalidate();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void populateData(Date startDate){
        RealmList<Quote> stockDataList = mRealmController.getStockData(stockSymbol).getStockList();
        Set.clear();
        for (int i = 0; i < stockDataList.size(); i++) {
            Quote quote = stockDataList.get(i);
            if (quote.getRealDate().after(startDate)){
                Set.addEntry(
                        new Entry(i, Float.valueOf(quote.getClose()) ,quote)
                );
            }
        }

        mLineData = new LineData(Set);
        mChart.setData(mLineData);
        mLineData.notifyDataChanged();
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void designGraph(){
        mChart.animateX(2500);
        YAxis yAxis = mChart.getAxisLeft();
        XAxis xAxis = mChart.getXAxis();
        Set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        Set.setDrawValues(false);
        Set.setDrawFilled(true);
        Set.setDrawCircles(false);
        Set.setColor(getColor(R.color.material_yellow));
        Set.setFillAlpha(255);
        xAxis.setDrawGridLines(true);
        yAxis.setDrawGridLines(true);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setEnabled(false);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setViewPortOffsets(0,0,0,0);
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.invalidate();
    }



    private class CustomMarkerView extends MarkerView{

        public CustomMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            Quote quote = (Quote)e.getData();
            String price = String.format(Locale.ENGLISH,"\t\t$%.2f",Float.valueOf(quote.getClose()));
            SimpleDateFormat SimpleFormat = new SimpleDateFormat("dd MMM yyyy",Locale.ENGLISH);
            String date = SimpleFormat.format(quote.getRealDate());
            stock_details.setText(date.concat(price));
        }
    }

}
