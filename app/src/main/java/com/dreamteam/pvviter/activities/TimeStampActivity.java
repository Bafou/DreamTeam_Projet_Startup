package com.dreamteam.pvviter.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.shawnlin.numberpicker.NumberPicker;
import com.dreamteam.pvviter.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeStampActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private NumberPicker numberPickerHours = null;
    private NumberPicker numberPickerMinutes = null;
    //minuteStepSize is the size set to get the wanted step between our minutes value
    //for example a minuteStepSize of 5 means 00 - 05 - 10 ...
    private final int minuteStepSize = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_time_stamp);

        customHoursNumberPicker();
        customMinutesNumberPicker();
        updateTimeStampEndValue();
        Button validateButton = (Button) findViewById(R.id.validate_button);
        validateButton.setVisibility(View.GONE);

    }

    /**
     * Customise the number picker for the hours
     */
    public void customHoursNumberPicker(){
        numberPickerHours = (NumberPicker)  findViewById(R.id.number_picker_hours);

        assert numberPickerHours != null;
        //used to format our values with 2 numbers
        numberPickerHours.setFormatter(new NumberPicker.Formatter() {
            @SuppressLint("DefaultLocale")
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        numberPickerHours.setOnValueChangedListener(this);

    }

    /**
     * Customise the number picker for minutes
     */
    public void customMinutesNumberPicker(){
        numberPickerMinutes = (NumberPicker)  findViewById(R.id.number_picker_minutes);

        assert numberPickerMinutes != null;
        numberPickerMinutes.setMaxValue(numberPickerMinutes.getMaxValue()/ minuteStepSize);

        assert numberPickerMinutes != null;
        //used to format our values with 2 numbers
        numberPickerMinutes.setFormatter(new NumberPicker.Formatter() {
            @SuppressLint("DefaultLocale")
            @Override
            public String format(int i) {
                return String.format("%02d", i * minuteStepSize);
            }
        });
        numberPickerMinutes.setOnValueChangedListener(this);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        updateTimeStampEndValue();
    }

    /**
     * Update the timestamp end value
     */
    public void updateTimeStampEndValue(){
        TextView timeStampEnd = (TextView) findViewById(R.id.time_stamp_end);
        String timeStampEndString = getResources().getString(R.string.time_stamp_end);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, numberPickerHours.getValue());
        c.add(Calendar.MINUTE, numberPickerMinutes.getValue()*minuteStepSize);
        timeStampEndString += " " + dateHourMinuteToString(c.getTime());
        assert timeStampEnd != null;
        timeStampEnd.setText(timeStampEndString);

        Button validateButton = (Button) findViewById(R.id.validate_button);
        assert validateButton != null;
        if(validateButton.getVisibility() == View.GONE) validateButton.setVisibility(View.VISIBLE);
    }

    /**
     * Concert a date into a string (only for hours and minutes)
     * @param date
     * @return
     */
    public String dateHourMinuteToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("H:mm", Locale.FRANCE);
        String date_s = null;
        try {
            date_s = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date_s;
    }

    /**
     * Open the map activity
     * @param view
     */
    public void openMapActivity(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
