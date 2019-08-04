package com.google.android.gms.fit.samples.basicsensorsapi.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.google.android.gms.fit.samples.basicsensorsapi.R;

public class ActivityEnter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);

        String title = "Войти";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        findViewById(R.id.b_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
            }
        });

        findViewById(R.id.tv_enother_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnter.this, R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Выход")
                        .setMessage("Выйти?")
                        .setCancelable(false)
                        .setNegativeButton("Нет",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("auth", "none").commit();
                                startActivity(new Intent(getApplicationContext(), CreateActivity.class));
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
