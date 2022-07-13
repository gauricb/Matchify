package com.example.matchify;

import static com.parse.ParseUser.getCurrentUser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.matchify.fragments.ProfileFragment;
import com.example.matchify.models.SpotifyUser;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import static com.example.matchify.MainActivity.currentSpotifyUser;


public class PreferenceActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        seekBarAge = findViewById(R.id.seekBarAge);
        titleAge = findViewById(R.id.titleAge);
        titleAgeRange = findViewById(R.id.titleAgeRange);
        inputAge = findViewById(R.id.etUserAge);
        displayAgeRange = findViewById(R.id.chosenAge);

        displayLocation = findViewById(R.id.chosenLocation);
        titleLocation = findViewById(R.id.titleLocation);
        seekBarLocation = findViewById(R.id.seekBarLocation);
        savePreferences = findViewById(R.id.buttonSavePrefs);

        seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                displayAgeRange.setText(String.valueOf(progress));
                currentSpotifyUser.setUserAgeRange(progress);
                currentSpotifyUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.e(TAG, "age range successfully saved to PARSE");
                    }
                });
                //save age range to parse

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
                currentSpotifyUser.setUserLocationRange(progress);
                currentSpotifyUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.e(TAG, "location range successfully saved to PARSE");
                    }
                });
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        savePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save input data to Parse for current user
                int getAge = Integer.parseInt(inputAge.getText().toString());
                currentSpotifyUser.setUserAge(getAge);
                currentSpotifyUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.e(TAG, "age successfully saved to PARSE");

                    }
                });




            }
        });



    }

}
