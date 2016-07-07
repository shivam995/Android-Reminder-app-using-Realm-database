package com.shivam.reminderapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.sql.Time;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "Log";
    EditText mTaskEditText;
    String mSelectedDate, mSelectedTime;
    TextView mDateTextView;
    TextView mTimeTextView;
    boolean isDateSelected = false;
    boolean isTimeSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        mDateTextView = (TextView) findViewById(R.id.dateTextView);
        mTimeTextView = (TextView) findViewById(R.id.timeTextView);
        mTaskEditText = (EditText) findViewById(R.id.editText);

        mDateTextView.setOnClickListener(this);
        findViewById(R.id.setReminder).setOnClickListener(this);
        mTimeTextView.setOnClickListener(this);
        setRepeatAlarm();


    }

    private void setRepeatAlarm() {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 202, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                AppConstant.REPEAT_ALARM_TIME_DIFF, alarmIntent);
    }

    private void onSetReminderClick() throws ParseException {

        if (TextUtils.isEmpty(mTaskEditText.getText())) {
            Toast.makeText(this, "Please enter task", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isDateSelected) {
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isTimeSelected) {
            Toast.makeText(this, "Please select time", Toast.LENGTH_SHORT).show();
            return;
        }

        String toParse = mSelectedDate + " " + mSelectedTime;
        SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        Date mdate = formatter.parse(toParse);
        ReminderOperations.addReminder(mDateTextView.getText().toString().trim(), mTimeTextView.getText().toString().trim(), mdate.getTime(), mTaskEditText.getText().toString());
        Toast.makeText(this, "Reminder has been set successfully !!", Toast.LENGTH_SHORT).show();
        mDateTextView.setText("Please select Date");
        mTimeTextView.setText("Please select time");
        isDateSelected = false;
        isTimeSelected = false;
        mSelectedDate = "";
        mSelectedTime = "";
    }

    public static String getDisplayDateFormat(String dateTimeStr) {
        String finalFormat = "";
        DateFormat fromFormat = new SimpleDateFormat("dd-MM-yyyy");
        fromFormat.setLenient(false);
        DateFormat toFormat = new SimpleDateFormat("dd-MMM-yyyy");
        toFormat.setLenient(false);
        try {
            Date date;
            date = fromFormat.parse(dateTimeStr);
            finalFormat = toFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return finalFormat;
    }

    private String getDisplayTimeFormat(int hr, int min) {
        Time tme = new Time(hr, min, 0);
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setReminder:
                try {
                    onSetReminderClick();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.timeTextView:
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                final int min = cal.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                isTimeSelected = true;
                                int m = minute - 10;
                                int h = hourOfDay;
                                if (m < 0 && h > 0) {
                                    h = h - 1;
                                    m = 60 + m;
                                } else {
                                    m = 0;
                                }
                                mSelectedTime = h + ":" + m;
                                Log.i(TAG, "onTimeSet: " + mSelectedTime);
                                mTimeTextView.setText(getDisplayTimeFormat(hourOfDay, minute));
                            }
                        }, hour, min, false);
                tpd.show();
                break;

            case R.id.dateTextView:

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                isDateSelected = true;
                                mSelectedDate = dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year;
                                mDateTextView.setText(getDisplayDateFormat(mSelectedDate));

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1);

                break;
            default:
                break;
        }
    }
}
