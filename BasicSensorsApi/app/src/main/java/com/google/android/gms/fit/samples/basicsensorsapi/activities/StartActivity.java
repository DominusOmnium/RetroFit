package com.google.android.gms.fit.samples.basicsensorsapi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.fit.samples.basicsensorsapi.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(String... strings) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String auth = sp.getString("auth", "none");
                if (auth == "none")
                    startActivity(new Intent(getApplicationContext(), CreateActivity.class));
                else
                    startActivity(new Intent(getApplicationContext(), ActivityEnter.class).putExtra("auth", auth));
                return null;
            }
        }.execute();
    }

}
