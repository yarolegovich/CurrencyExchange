package com.yarolegovich.currencyexchange.view;

/**
 * Created by yarolegovich on 22.02.2017.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.yarolegovich.currencyexchange.R;
import com.yarolegovich.currencyexchange.util.Logger;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateRangePicker extends TextView {

    private Listener listener;

    private DateFormat dateFormat;
    private String delimeter;
    private FragmentManager fragmentManager;

    public DateRangePicker(Context context) {
        this(context, null);
    }

    public DateRangePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateRangePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DateRangePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Typeface typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);
        setTypeface(typeface);
        setGravity(Gravity.CENTER_VERTICAL);

        delimeter = " \u2014 ";

        dateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.US);
        DateTime startDate = new DateTime().minusDays(12).withTimeAtStartOfDay();
        DateTime endDate = new DateTime().plusDays(1).withTimeAtStartOfDay();
        setDates(startDate, endDate);

        if (!isInEditMode()) {
            fragmentManager = ((Activity) getContext()).getFragmentManager();
        }

        setMovementMethod(LinkMovementMethod.getInstance());
        setSpans();
    }

    public Date getToDate() {
        try {
            String dateString = getText().toString();
            dateString =  dateString.substring(dateString.indexOf(delimeter) + delimeter.length());
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            Logger.e(e);
            return new Date();
        }
    }

    public Date getFromDate() {
        try {
            String dateString = getText().toString();
            dateString = dateString.substring(0, dateString.indexOf(delimeter));
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            Logger.e(e);
            return new Date();
        }
    }

    public void setDates(DateTime fromDate, DateTime toDate) {
        setText(dateFormat.format(fromDate.toDate()) + delimeter +
                dateFormat.format(toDate.toDate()), BufferType.SPANNABLE);
        setSpans();
    }

    private void setSpans() {
        Spannable spannable = (Spannable) getText();
        setSpan(spannable, DateOrder.FIRST);
        setSpan(spannable, DateOrder.SECOND);
    }

    private void addDropDownArrow(Spannable span, int position) {
        ImageSpan pickerArrow = new ImageSpan(getContext(), R.drawable.ic_arrow_drop_down_white_18dp);
        span.setSpan(pickerArrow, position, position + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private void setSpan(Spannable span, DateOrder dateOrder) {
        String str = span.toString();
        int start = dateOrder == DateOrder.FIRST ? 0 : str.indexOf(delimeter) + delimeter.length();
        int end = dateOrder == DateOrder.FIRST ? span.toString().indexOf(delimeter) : span.length();
        addDropDownArrow(span, end - 1);
        span.setSpan(
                new OpenPickerOnClickSpan(dateOrder), start,
                end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    public void setListener(Listener dateChangeListener) {
        listener = dateChangeListener;
    }

    @SuppressWarnings("WeakerAccess")
    private class OpenPickerOnClickSpan extends ClickableSpan implements DatePickerDialog.OnDateSetListener {

        private DateOrder controlsDate;


        public OpenPickerOnClickSpan(DateOrder dateOrder) {
            this.controlsDate = dateOrder;
        }

        @Override
        public void onClick(View widget) {
            DateTime dateTime = new DateTime(controlsDate == DateOrder.FIRST ?
                    getFromDate() :
                    getToDate());

            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this, dateTime.getYear(), dateTime.getMonthOfYear() - 1,
                    dateTime.getDayOfMonth());

            dpd.vibrate(false);
            dpd.show(fragmentManager, widget.getContext().getString(R.string.dialog_date_title));
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
        }

        @Override
        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
            DateTime first, second;
            if (controlsDate == DateOrder.FIRST) {
                first = new DateTime(year, month + 1, day, 4, 4);
                second = new DateTime(getToDate());
            } else {
                first = new DateTime(getFromDate());
                second = new DateTime(year, month + 1, day, 4, 4);
            }
            if (first.isBefore(second)) {
                setDates(first, second);
                if (listener != null) {
                    listener.onDateChanged(first.toDate(), second.toDate());
                }
            }
        }
    }

    private enum DateOrder {FIRST, SECOND}

    public interface Listener {
        void onDateChanged(Date from, Date to);
    }
}
