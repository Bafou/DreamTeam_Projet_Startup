package com.dreamteam.pvviter.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnlin.numberpicker.NumberPicker;
import com.dreamteam.pvviter.R;
import java.util.Calendar;
import fragments.TimePickerFragment;
import utils.DateManipulation;


/**
 * Created by FlorianDoublet on 28/10/2016.
 */
public class TimeStampActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private NumberPicker numberPickerHours = null;
    private NumberPicker numberPickerMinutes = null;
    //minuteStepSize is the size set to get the wanted step between our minutes value
    //for example a minuteStepSize of 5 means 00 - 05 - 10 ...
    private static int defaultMinuteStepSize = 5;
    private static int minuteStepSize = defaultMinuteStepSize;
    private TimePickerFragment timePickerFragment = new TimePickerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_stamp);

        customHoursNumberPicker();
        customMinutesNumberPicker();
        updateTimeStampEndValue();
        Button validateButton = (Button) findViewById(R.id.validate_button);
        validateButton.setVisibility(View.GONE);

    }

    public NumberPicker getNumberPickerMinutes() {
        return numberPickerMinutes;
    }

    public NumberPicker getNumberPickerHours() {
        return numberPickerHours;
    }

    public static int getMinuteStepSize() {
        return minuteStepSize;
    }

    /**
     * Customise the number picker for the hours
     */
    public void customHoursNumberPicker(){
        numberPickerHours = (NumberPicker)  findViewById(R.id.number_picker_hours);
        this.minuteStepSize = defaultMinuteStepSize;

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

    /**
     * Change the step value before the customisation
     * @param minuteStepSize
     */
    public void customMinutesNumberPicker(int minuteStepSize){
        this.minuteStepSize = minuteStepSize;
        this.numberPickerMinutes.setMaxValue(59);
        customMinutesNumberPicker();
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
        Calendar newCal = Calendar.getInstance();

        int hoursToAdd = numberPickerHours.getValue();
        int minutesToAdd = numberPickerMinutes.getValue()*minuteStepSize;
        newCal.add(Calendar.HOUR_OF_DAY, hoursToAdd);
        newCal.add(Calendar.MINUTE, minutesToAdd);

        //test if it's tomorrow
        if(DateManipulation.isTomorrow(newCal)){
            timeStampEndString += " " + getResources().getString(R.string.tomorrow_sentence);
        }
        timeStampEndString += " " + DateManipulation.dateHourMinuteToString(newCal.getTime());
        assert timeStampEnd != null;
        timeStampEnd.setText(timeStampEndString);

        Button validateButton = (Button) findViewById(R.id.validate_button);
        assert validateButton != null;
        if(validateButton.getVisibility() == View.GONE) validateButton.setVisibility(View.VISIBLE);
    }


    /**
     * Open the map activity
     * @param view
     */
    public void openMapActivity(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void openTimePickerDialog(View view){
        timePickerFragment.setTimeStampActivity(this);
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    public void takeAPicture(View view){
        Context context = getApplicationContext();
        CharSequence text = "Click click :D";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        //TODO: implement here the photo activity
    }


}
