package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.udacity.stockhawk.data.Contract;

/**
 * Created by sai on 1/3/17.
 */

public class StockWidgetIntentService extends IntentService{



    public StockWidgetIntentService() {
        super("StockWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockHawkWidgetProvider.class));

        Uri stocksUri = Contract.Quote.URI;
        Cursor data = getContentResolver().query(stocksUri, STOCK_COLUMNS, null, null, null);

        if(data == null) {
            return;
        }

        if(!data.moveToFirst()) {
            data.close();
            return;
        }

        String symbolName = data.getS
    }
}
