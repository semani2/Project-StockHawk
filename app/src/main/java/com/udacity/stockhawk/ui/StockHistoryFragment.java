package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sai on 1/5/17.
 */

public class StockHistoryFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String SYMBOL_KEY = "symbol";

    private String symbol = null;

    private static final int STOCK_DETAILS_LOADER = 0;

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_NAME,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_HISTORY
    };

    private static final int INDEX_STOCK_SYMBOL = 0;
    private static final int INDEX_STOCK_PRICE = 1;
    private static final int INDEX_STOCK_NAME = 2;
    private static final int INDEX_STOCK_CHANGE = 3;
    private static final int INDEX_STOCK_HISTORY = 4;

    private final DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    // View bindings
    @BindView(R.id.closeButton)
    ImageView closeButton;

    @BindView(R.id.symbol)
    TextView stockSymbolTextView;

    @BindView(R.id.price)
    TextView stockPriceTextView;

    @BindView(R.id.change)
    TextView changePriceTextView;

    @BindView(R.id.stocksChart)
    GraphView stockHistoryChart;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.name)
    TextView nameTextView;

    static StockHistoryFragment newInstance(String stockSym) {
        StockHistoryFragment fragment = new StockHistoryFragment();

        Bundle args = new Bundle();
        args.putString(SYMBOL_KEY, stockSym);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(STOCK_DETAILS_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments() == null || getArguments().getString(SYMBOL_KEY, null) == null) {
            dismissAllowingStateLoss();
        }

        symbol = getArguments().getString(SYMBOL_KEY);

        View v = inflater.inflate(R.layout.dialog_fra_stock_history, container, false);

        ButterKnife.bind(this, v);

        progressBar.setVisibility(View.VISIBLE);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchStockData();
    }

    private void fetchStockData() {
        if(null == symbol) {
            return;
        }

        getLoaderManager().restartLoader(STOCK_DETAILS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null == symbol) {
            return null;
        }

        return new CursorLoader(getActivity(),
                Contract.Quote.makeUriForStock(symbol),
                STOCK_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        progressBar.setVisibility(View.GONE);
        initStockView(data);
    }

    private void initStockView(Cursor data) {
        if(!data.moveToFirst()) {
            return;
        }

        String symbol = data.getString(INDEX_STOCK_SYMBOL);
        String price = data.getString(INDEX_STOCK_PRICE);
        Float change = data.getFloat(INDEX_STOCK_CHANGE);
        String history = data.getString(INDEX_STOCK_HISTORY);
        String name = data.getString(INDEX_STOCK_NAME);

        if (change > 0) {
            changePriceTextView.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            changePriceTextView.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String changeText = dollarFormatWithPlus.format(change);

        stockSymbolTextView.setText(symbol);
        stockPriceTextView.setText(price);
        changePriceTextView.setText(changeText);
        nameTextView.setText(name);

        history = history.replace("\n", " ");
        history = history.replace(",", "");
        String[] historyData = history.split(" ");
        List<StockHistoryData> dataList = new ArrayList<>();

        for(int i = historyData.length - 1; i>0 ;i = i - 2) {
            StockHistoryData stockHistoryData = new StockHistoryData();
            stockHistoryData.setDate(Double.parseDouble(historyData[i-1]));
            stockHistoryData.setPrice(Double.parseDouble(historyData[i]));
            dataList.add(stockHistoryData);
        }

        DataPoint[] dataPoints = new DataPoint[dataList.size()];

        int i = 0;
        for(StockHistoryData stockHistoryData : dataList) {
            dataPoints[i] = new DataPoint(i, stockHistoryData.getPrice());
            i++;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

        stockHistoryChart.setTitle("Price over the past couple of years");
        stockHistoryChart.addSeries(series);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nothing to here
    }

    private class StockHistoryData {
        Double date;
        Double price;

        public Double getDate() {
            return date;
        }

        public void setDate(Double date) {
            this.date = date;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }
}
