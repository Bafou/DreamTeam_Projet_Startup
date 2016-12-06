package fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.dreamteam.pvviter.R;
import com.dreamteam.pvviter.activities.SettingsActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import net.jayschwa.android.preference.SliderPreference;


/**
 * Created by Geoffrey on 12/11/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        Preference button = findPreference(getString(R.string.pref_raz));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                confirmDialogRAZ(getActivity());
                return true;
            }
        });

        SliderPreference sliderWalkingSpeed = (SliderPreference) findPreference(getString(R.string.pref_walking_speed));
        sliderWalkingSpeed.setTitle(String.format(getString(R.string.slider_walking_speed_title).toString(), getPreferenceManager().getSharedPreferences().getFloat(sliderWalkingSpeed.getKey(),0)*10));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    //method to save preferences
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {

        if(key.equals(getString(R.string.pref_walking_speed))){
            SliderPreference sliderWalkingSpeed = (SliderPreference) findPreference(getString(R.string.pref_walking_speed));
            sliderWalkingSpeed.setTitle(String.format(getString(R.string.slider_walking_speed_title).toString(), getPreferenceManager().getSharedPreferences().getFloat(sliderWalkingSpeed.getKey(),0)*10));
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Vitesse : " + sharedPreferences.getFloat(key, -1), Toast.LENGTH_SHORT);
            toast.show();
        }
        if(key.equals(getString(R.string.pref_raz))){
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Délai : " + sharedPreferences.getFloat(key, -1), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void confirmDialogRAZ(Context context) {
        final AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle("Avertissement");
        alert.setMessage("Voulez-vous rétablir vos paramètres par défaut ?");
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Oui",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanSettings();

                        alert.dismiss();
                    }
                });

        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Non",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        alert.dismiss();

                    }
                });

        alert.show();
    }

    private void cleanSettings(){
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        preferencesEditor.clear();
        preferencesEditor.commit();
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Paramètres par défaut restaurés", Toast.LENGTH_SHORT);
        toast.show();

        Intent startIntent = new Intent(getActivity(), SettingsActivity.class);
        getActivity().overridePendingTransition(0, 0);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
        getActivity().startActivity(startIntent);
    }
}