package com.pcare.common.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @Author: gl
 * @CreateDate: 2019/11/12
 * @Description:
 */
public class YearPickerDialog extends DatePickerDialog {
    private int selectYear;
    public YearPickerDialog(Context context, int themeResId) {
        super(context,themeResId);
//        getDatePicker().setCalendarViewShown(false);
//        ((ViewGroup)((ViewGroup)getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2)
//                .setVisibility(View.GONE);
//        ((ViewGroup)((ViewGroup)getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(1)
//                .setVisibility(View.GONE);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int dayOfMonth) {
        selectYear = year;
    }

    public int getYear(){
        return selectYear;
    }

}
