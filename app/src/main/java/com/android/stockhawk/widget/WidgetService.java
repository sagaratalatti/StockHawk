package com.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.stockhawk.R;
import com.android.stockhawk.data.QuoteColumns;
import com.android.stockhawk.data.QuoteProvider;
import com.android.stockhawk.rest.Constants;

/**
 * Created by sagar_000 on 11/15/2016.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetService extends RemoteViewsService {

    public WidgetService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetItemRemoteView(this.getApplicationContext(),intent);
    }

    class WidgetItemRemoteView implements RemoteViewsService.RemoteViewsFactory{
        Context mContext;
        Cursor mCursor;
        Intent mIntent;

        public WidgetItemRemoteView(Context mContext, Intent mIntent) {
            this.mContext = mContext;
            this.mIntent = mIntent;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public int getCount() {
            return mCursor != null ? mCursor.getCount() : 0;
        }

        @Override
        public void onDataSetChanged() {
            if (mCursor!=null)
                mCursor.close();

            final long pId = Binder.clearCallingIdentity();

            mCursor = getContentResolver().query(
                    QuoteProvider.Quotes.CONTENT_URI,
                    null,
                    QuoteColumns.ISCURRENT + " = ?",
                    new String[]{"1"},
                    null
            );

            Binder.restoreCallingIdentity(pId);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            try{
                mCursor.moveToPosition(position);
                int changeColor;

                // get Stock Quote information
                String stockSymbol = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL));
                String stockBidPrice = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE));
                String stockPriceChange = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE));
                int isUp = mCursor.getInt(mCursor.getColumnIndex(QuoteColumns.ISUP));

                RemoteViews listItem = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
                listItem.setTextViewText(R.id.stock_symbol,stockSymbol);
                listItem.setTextViewText(R.id.bid_price,stockBidPrice);
                listItem.setTextViewText(R.id.change,stockPriceChange);


                if (isUp == 1)
                    changeColor = R.drawable.percent_change_pill_green;
                else
                    changeColor = R.drawable.percent_change_pill_red;
                listItem.setInt(R.id.change,"setBackgroundResource", changeColor);

                Intent intent = new Intent();
                intent.putExtra(Constants.STOCK_KEY, stockSymbol);
                listItem.setOnClickFillInIntent(R.id.list_item_stock_quote, intent);
                return listItem;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(mCursor.getColumnIndex(QuoteColumns._ID));
        }

        @Override
        public void onDestroy() {
            if (mCursor!=null)
                mCursor.close();
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
