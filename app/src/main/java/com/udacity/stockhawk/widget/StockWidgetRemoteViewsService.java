package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

/**
 * Created by sai on 1/3/17.
 */

public class StockWidgetRemoteViewsService extends RemoteViewsService {

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_NAME
    };

    private static final int INDEX_STOCK_SYMBOL = 0;
    private static final int INDEX_STOCK_PRICE = 1;
    private static final int INDEX_STOCK_NAME = 2;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do here
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();


                data = getContentResolver().query(Contract.Quote.URI, STOCK_COLUMNS,
                        null,
                        null,
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);
                String symbol = data.getString(INDEX_STOCK_SYMBOL);
                float stockPrice = data.getFloat(INDEX_STOCK_PRICE);
                String stockName = data.getString(INDEX_STOCK_NAME);

                views.setTextViewText(R.id.widget_symbol_text, symbol);
                views.setTextViewText(R.id.widget_price_text, String.valueOf(stockPrice));

                setViewsContentDesc(views, stockName);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            private void setViewsContentDesc(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_symbol_text, description);
            }
        };
    }
}
