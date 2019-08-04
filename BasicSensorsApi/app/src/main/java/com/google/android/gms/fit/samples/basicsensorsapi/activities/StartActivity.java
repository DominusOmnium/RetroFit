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

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(String... strings) {
                String auth = "";
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                pref.getString("auth", auth);
                if (auth == "")
                    startActivity(new Intent(getApplicationContext(), CreateActivity.class));
                else
                    startActivity(new Intent(getApplicationContext(), ActivityEnter.class).putExtra("auth", auth));
                return null;
            }
        }.execute();
    }

}
