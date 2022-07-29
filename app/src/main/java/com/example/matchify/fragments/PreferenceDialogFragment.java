package com.example.matchify.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.matchify.R;
import com.parse.ParseException;
import com.parse.SaveCallback;

import static com.example.matchify.MainActivity.currentSpotifyUser;
import static com.example.matchify.MainActivity.getCurrentSpotifyUser;

public class PreferenceDialogFragment extends DialogFragment {

    public static final String TAG = "PreferenceActivity";
    public static final int MAX_AGE = 100;
    public static final int MIN_AGE = 18;

    SeekBar seekBarAge;
    SeekBar seekBarLocation;
    TextView displayLocation;
    TextView displayAgeRange;
    TextView titleAge;
    TextView titleLocation;
    EditText inputAge;
    TextView titleAgeRange;
    Button savePreferences;
    Context context;


    public PreferenceDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static PreferenceDialogFragment newInstance(String title) {
        PreferenceDialogFragment preferenceDialogFragment = new PreferenceDialogFragment();
        Bundle args = new Bundle();
        args.putString("make your choices", title);
        preferenceDialogFragment.setArguments(args);
        return preferenceDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.preference_dialog, container, false);
    }
    public boolean isUserNull() throws ParseException {
        return getCurrentSpotifyUser().size() == 0;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seekBarAge = view.findViewById(R.id.seekBarAge);
        titleAge = view.findViewById(R.id.titleAge);
        titleAgeRange = view.findViewById(R.id.titleAgeRange);
        inputAge = view.findViewById(R.id.etUserAge);
        displayAgeRange = view.findViewById(R.id.chosenAge);

        displayLocation = view.findViewById(R.id.chosenLocation);
        titleLocation = view.findViewById(R.id.titleLocation);
        seekBarLocation = view.findViewById(R.id.seekBarLocation);
        savePreferences = view.findViewById(R.id.buttonSavePrefs);

        seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                displayAgeRange.setText(String.valueOf(progress));

                Log.e(TAG, "^^^" + currentSpotifyUser);

                if (currentSpotifyUser != null) {
                    currentSpotifyUser.setUserAgeRange(progress);
                    currentSpotifyUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                           Log.e(TAG, "age range successfully saved to PARSE for " + currentSpotifyUser.getUserName());

                        }
                    });
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekBarLocation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                displayLocation.setText(String.valueOf(progress));

                if (currentSpotifyUser != null) {
                    currentSpotifyUser.setUserLocationRange(progress);
                    currentSpotifyUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            Log.e(TAG, "age range successfully saved to PARSE for " + currentSpotifyUser.getUserName());

                        }
                    });
                }


            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        savePreferences.setBackgroundColor(getResources().getColor(R.color.pink));
        savePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save input data to Parse for current user
                int getAge = Integer.parseInt(inputAge.getText().toString());

                if (currentSpotifyUser != null) {
                    currentSpotifyUser.setUserAge(getAge);
                    currentSpotifyUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            Log.e(TAG, "age range successfully saved to PARSE for " + currentSpotifyUser.getUserName());

                        }
                    });
                }

                dismiss();



            }
        });
    }



}
