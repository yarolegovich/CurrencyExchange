package com.yarolegovich.currencyexchange;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.yarolegovich.currencyexchange.model.Currency;
import com.yarolegovich.currencyexchange.model.ExchangeData;
import com.yarolegovich.currencyexchange.model.ExchangeRate;
import com.yarolegovich.currencyexchange.net.ExchangeRates;
import com.yarolegovich.currencyexchange.util.Utils;
import com.yarolegovich.currencyexchange.view.CurrencyPicker;
import com.yarolegovich.currencyexchange.view.DateRangePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.yarolegovich.currencyexchange.util.Utils.format;
import static com.yarolegovich.currencyexchange.util.Utils.tintDrawables;

public class MainActivity extends AppCompatActivity implements ExchangeRates.ResultListener,
        ExchangeRates.ErrorListener, OnChartValueSelectedListener,
        CurrencyPicker.Listener, DateRangePicker.Listener {

    private LineChart lineChart;

    private TextView closeRateView;
    private TextView dayDiffView;
    private TextView dayHighView;
    private TextView dayLowView;

    private CurrencyPicker currencyPicker;
    private DateRangePicker dateRangePicker;

    private ExchangeRates exchangeRates;

    private Dialog progressDialog;
    private Utils utils;
    private Drawable arrowUp, arrowDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utils = new Utils(this);
        arrowUp = utils.drawable(R.drawable.ic_arrow_drop_up_white_24dp);
        arrowDown = utils.drawable(R.drawable.ic_arrow_drop_down_white_18dp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lineChart = (LineChart) findViewById(R.id.chart_rates);
        configureChart(lineChart);

        currencyPicker = (CurrencyPicker) findViewById(R.id.picker_currency);
        currencyPicker.setListener(this);

        dateRangePicker = (DateRangePicker) findViewById(R.id.picker_date_range);
        dateRangePicker.setListener(this);

        closeRateView = (TextView) findViewById(R.id.rate_closed);
        dayDiffView = (TextView) findViewById(R.id.rate_closed_diff);
        dayHighView = (TextView) findViewById(R.id.rate_high);
        dayLowView = (TextView) findViewById(R.id.rate_low);

        exchangeRates = ExchangeRates.create();
        exchangeRates.setErrorListener(this);
        exchangeRates.setResultListener(this);

        queryData();
    }

    private void queryData() {
        showProgressDialog();
        exchangeRates.getHistoricalRates(
                currencyPicker.getSelectedCurrency(),
                dateRangePicker.getFromDate(),
                dateRangePicker.getToDate());
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        ExchangeRate rate = (ExchangeRate) e.getData();
        int rateColor = rate.isGrowing() ?
                utils.color(R.color.materialGreen) :
                utils.color(R.color.materialRed);

        closeRateView.setText(format(rate.getCloseRate()));
        closeRateView.setCompoundDrawablesWithIntrinsicBounds(
                null, null, rate.isGrowing() ? arrowUp : arrowDown,
                null);
        tintDrawables(closeRateView, rateColor);

        dayDiffView.setText(format(rate.getDiff()));
        dayDiffView.setTextColor(rateColor);

        dayHighView.setText(format(rate.getHighRate()));
        dayLowView.setText(format(rate.getLowRate()));
    }

    @Override
    public void onExchangeDataReady(ExchangeData data) {
        hideProgressDialog();

        int time = 0;
        List<Entry> entries = new ArrayList<>();
        for (ExchangeRate rate : data.getExchangeRates()) {
            entries.add(new Entry(time++, (float) rate.getCloseRate(), rate));
        }
        LineDataSet dataSet = new LineDataSet(entries, data.getToCurrency().name());
        configureDataSet(dataSet);

        LineData lineData = new LineData(dataSet);
        lineData.setValueTextColor(Color.WHITE);

        lineChart.setData(lineData);
        lineChart.highlightValue(time - 1, 0);
        lineChart.invalidate();
    }


    @Override
    public void onCurrencySelected(Currency currency) {
        queryData();
    }

    @Override
    public void onDateChanged(Date from, Date to) {
        queryData();
    }

    @Override
    public void onError(Throwable e) {
        hideProgressDialog();
        Toast.makeText(
                this, e.getMessage(),
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exchangeRates.setErrorListener(null);
        exchangeRates.setResultListener(null);
    }

    private void configureChart(LineChart chart) {
        chart.setOnChartValueSelectedListener(this);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.getXAxis().setEnabled(false);
    }

    private void configureDataSet(LineDataSet dataSet) {
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(false);
        dataSet.setFillColor(utils.color(R.color.primaryTeal));
        dataSet.setColor(utils.color(R.color.darkPrimaryTeal));
    }

    private void showProgressDialog() {
        hideProgressDialog();
        progressDialog = ProgressDialog.show(
                this, getString(R.string.dialog_loading_title),
                "", true, false);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void onNothingSelected() {

    }
}
