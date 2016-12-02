package com.dreamteam.pvviter.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnlin.numberpicker.NumberPicker;
import com.dreamteam.pvviter.R;
import java.util.Calendar;

import fragments.TimePickerFragment;
import services.File_IO;
import utils.Data_Storage;
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
    private boolean changeTimeMode=false;
    private long parkingTimeStore;
    private String previousActivityName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_time_stamp);

        Intent intent = getIntent();
        previousActivityName = intent.getStringExtra("activity");

        //if the activity is call by "change_time" (map button)
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            changeTimeMode = bundle.getBoolean("changeTimeMode");
        }
        if(changeTimeMode){
            parkingTimeStore=getDataStorageParkingTime();

            ImageButton carPictureButton = (ImageButton) findViewById(R.id.car_picture_button);
            carPictureButton.setVisibility(View.INVISIBLE);
            carPictureButton.setEnabled(false);

            TextView timeStampLabel = (TextView) findViewById(R.id.time_stamp_title);
            timeStampLabel.setText(R.string.add_car_time_label);
        }

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

    public boolean isChangeTimeMode(){ return changeTimeMode;}

    /**
     * Customise the number picker for the hours
     */
    public void customHoursNumberPicker(){
        numberPickerHours = (NumberPicker)  findViewById(R.id.number_picker_hours);
        minuteStepSize = defaultMinuteStepSize;

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
        TimeStampActivity.minuteStepSize = minuteStepSize;
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
        //Log.d("minuteStepSize",""+minuteStepSize );
        
        Calendar newCal = Calendar.getInstance();
        //if the user want change parking time
        if(changeTimeMode){
            newCal.setTimeInMillis( parkingTimeStore);
        }
        int hoursToAdd = numberPickerHours.getValue();
        int minutesToAdd = numberPickerMinutes.getValue()*minuteStepSize;
        newCal.add(Calendar.HOUR_OF_DAY, hoursToAdd);
        newCal.add(Calendar.MINUTE, minutesToAdd);


        //Change the message if it's tomorrow
        if(DateManipulation.isTomorrow(newCal))
            timeStampEndString += " " + getResources().getString(R.string.tomorrow_sentence);

        timeStampEndString += " " + DateManipulation.dateHourMinuteToString(newCal.getTime());

        //Change the message if it's after tomorrow
        if(DateManipulation.isAfterTomorrow(newCal))
            timeStampEndString = getResources().getString(R.string.time_stamp_end) + " " + DateManipulation.dayAndMonthToString(newCal.getTime())
                    + " " + getResources().getString(R.string.time_stamp_a) + " "  + DateManipulation.dateHourMinuteToString(newCal.getTime());

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
        Calendar newCal = Calendar.getInstance();

        //if the user want add car park time
        if(changeTimeMode){
            newCal.setTimeInMillis( parkingTimeStore );
        }

        int hoursToAdd = numberPickerHours.getValue();
        int minutesToAdd = numberPickerMinutes.getValue()*minuteStepSize;
        newCal.add(Calendar.HOUR_OF_DAY, hoursToAdd);
        newCal.add(Calendar.MINUTE, minutesToAdd);
        Data_Storage.set_parking_end_time_in_milliseconds(getApplicationContext(), newCal.getTimeInMillis());

        if(isChangeTimeMode()){
            //return intent for call onActivityResult method on MapActivity
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }else {
            Intent intent = new Intent(this, MapActivity.class);
            //Used to know that the next activity was called by TimeStampActivity
            intent.putExtra("activity",getString(R.string.title_activity_time_stamp));
            startActivity(intent);

        }

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

    public long getDataStorageParkingTime(){
        return Data_Storage.get_parking_end_time_in_milliseconds(getApplicationContext());
    }
    public long getParkingTimeStore(){
        return parkingTimeStore;
    }

    @Override
    public void onBackPressed() {
        String title = getString(R.string.title_alert_dialog_exit_application);
        String message = getString(R.string.message_alert_dialog_exit_application);
        String positiveButton = getString(R.string.positive_button_alert_dialog);
        String negativeButton = getString(R.string.negative_button_alert_dialog);
        AlertDialog.Builder alertDialog;

        if(previousActivityName != null){
            if(previousActivityName.equals(getString(R.string.title_activity_start_activity))){
                title = getString(R.string.title_alert_dialog_back_to_start_activity);
                message = getString(R.string.message_alert_dialog_back_to_start_activity);
            } else if(previousActivityName.equals(getString(R.string.title_activity_map))){
                finish();
                return;
            }

            alertDialog = resetOfDataAlertDialog(title,message,positiveButton,negativeButton);
        } else {
            alertDialog = moveTaskToBackAlertDialog(title,message,positiveButton,negativeButton);
        }
        alertDialog.show();
    }

    /**
     * Build an AlertDialog when we want to reset the data and go back to the startActivity
     * @param title of the alert
     * @param message of the alert
     * @param positiveButton of the alert
     * @param negativeButton of the alert
     * @return
     */
    private AlertDialog.Builder resetOfDataAlertDialog(String title, String message, String positiveButton, String negativeButton){
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File_IO.delete_all_files(getApplicationContext());

                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                })
                .setNegativeButton(negativeButton, null);
    }

    /**
     * Build an AlertDialog when we want to move the task back
     * @param title of the alert
     * @param message of the alert
     * @param positiveButton of the alert
     * @param negativeButton of the alert
     * @return
     */
    private AlertDialog.Builder moveTaskToBackAlertDialog(String title, String message, String positiveButton, String negativeButton){
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                    }
                })
                .setNegativeButton(negativeButton, null);
    }

}
