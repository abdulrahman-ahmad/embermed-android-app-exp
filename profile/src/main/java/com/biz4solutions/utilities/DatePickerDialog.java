package com.biz4solutions.utilities;

import android.content.Context;

import com.biz4solutions.activities.ProfileActivity;
import com.biz4solutions.profile.R;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;

import java.util.Calendar;

/**
 * Created by saurabh.asati on 8/13/2018.
 */

public class DatePickerDialog implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private CustomDateInterface customDateInterface;
    private Context context;
    private Long previousSelectedDateValue = null;


    public void registerListener(CustomDateInterface customDateInterface, Context context) {
        this.customDateInterface = customDateInterface;
        this.context = context;
    }

    public void CustomDatePickerDialog() {

    }

    public void showDatePickerDialog(boolean is18LimitEnabled) {
        Calendar todayDate = Calendar.getInstance();
        Calendar previousSelectedDate = Calendar.getInstance();
        int previousSelectedDateYear, previousSelectedDateMonth, previousSelectedDateDay;
        if (previousSelectedDateValue != null) {
            previousSelectedDate.setTimeInMillis(previousSelectedDateValue);
            previousSelectedDateYear = previousSelectedDate.get(Calendar.YEAR);
            previousSelectedDateMonth = previousSelectedDate.get(Calendar.MONTH);
            previousSelectedDateDay = previousSelectedDate.get(Calendar.DAY_OF_MONTH);
        } else {
            previousSelectedDate.setTimeInMillis(todayDate.getTimeInMillis());
            previousSelectedDate.setTimeInMillis(todayDate.getTimeInMillis());
            previousSelectedDateYear = previousSelectedDate.get(Calendar.YEAR) - 18;
            previousSelectedDateMonth = previousSelectedDate.get(Calendar.MONTH);
            previousSelectedDateDay = previousSelectedDate.get(Calendar.DAY_OF_MONTH);
        }

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTimeInMillis(todayDate.getTimeInMillis());
        lastDate.add(Calendar.DAY_OF_YEAR, 30);
        showDialog(todayDate, lastDate, previousSelectedDateYear, previousSelectedDateMonth, previousSelectedDateDay, is18LimitEnabled);
    }

    private void showDialog(Calendar todayDate, Calendar lastDate, int previousSelectedDateYear, int previousSelectedDateMonth, int previousSelectedDateDay, boolean is18LimitEnabled) {

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(this)
                .setDoneText("OK")
                .setCancelText("CANCEL")
                .setThemeCustom(R.style.MyCustomBetterPickersDialogs)
                .setPreselectedDate(previousSelectedDateYear, previousSelectedDateMonth, previousSelectedDateDay)
                .setDateRange(null, new MonthAdapter.CalendarDay(is18LimitEnabled ? todayDate.get(Calendar.YEAR) - 18 : todayDate.get(Calendar.YEAR), todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DAY_OF_MONTH)));
        showCalender(cdp);
    }

    private void showCalender(CalendarDatePickerDialogFragment cdp) {
        if (context instanceof ProfileActivity)
            cdp.show(((ProfileActivity) context).getSupportFragmentManager(), "fragment");
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        String formattedDate = DateUtility.formatDate(year, monthOfYear, dayOfMonth);
        String selectedDateToSendServer = DateUtility.formatDateOfBirthToSendBackend(year, monthOfYear, dayOfMonth);
        Calendar previousDate = Calendar.getInstance();
        previousDate.set(year, monthOfYear, dayOfMonth);
        if (customDateInterface != null)
            customDateInterface.onDateSetListener(selectedDateToSendServer, formattedDate);
    }

    public interface CustomDateInterface {
        void onDateSetListener(String selectedDateToSendServer, String formattedDate);
    }
}

