package com.yarolegovich.currencyexchange.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yarolegovich.currencyexchange.R;
import com.yarolegovich.currencyexchange.model.Currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarolegovich on 22.02.2017.
 */

public class CurrencyPicker extends LinearLayout {

    private int colorIconInactive;
    private int colorIconActive;

    private SelectionManager selectionManager;

    private Listener listener;

    public CurrencyPicker(Context context) {
        super(context);
    }

    public CurrencyPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CurrencyPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CurrencyPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        selectionManager = new SelectionManager();

        colorIconActive = ContextCompat.getColor(getContext(), R.color.currencyActive);
        colorIconInactive = ContextCompat.getColor(getContext(), R.color.currencyInactive);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        int[] icons = {
                R.drawable.ic_hryvnia, R.drawable.ic_currency_eur_white_24dp,
                R.drawable.ic_currency_rub_white_24dp, R.drawable.ic_currency_gbp_white_24dp};

        Currency[] currencies = {Currency.UAH, Currency.EUR, Currency.RUB, Currency.GBP};

        for (int i = 0; i < icons.length; i++) {
            ImageView iconView = new ImageView(getContext());
            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            iconView.setLayoutParams(params);
            iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iconView.setImageResource(icons[i]);
            iconView.setTag(currencies[i]);
            iconView.setClickable(true);
            iconView.setColorFilter(colorIconInactive);
            addView(iconView);

            iconView.setOnClickListener(selectionManager);
        }

        selectionManager.onClick(getChildAt(0));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Currency getSelectedCurrency() {
        return (Currency) selectionManager.selected.getTag();
    }

    private class SelectionManager implements OnClickListener {

        private ImageView selected;

        @Override
        public void onClick(View v) {
            ImageView previous = selected;
            if (selected != v) {
                selected = (ImageView) v;
                selected.setColorFilter(colorIconActive);
                if (listener != null) {
                    Currency currency = (Currency) selected.getTag();
                    listener.onCurrencySelected(currency);
                }
                if (previous != null) {
                    previous.setColorFilter(colorIconInactive);
                }
            }
        }
    }

    public interface Listener {
        void onCurrencySelected(Currency currency);
    }
}
