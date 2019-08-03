package com.google.android.gms.fit.samples.basicsensorsapi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.fit.samples.basicsensorsapi.activities.CreateActivity;

public class CheckData extends AppCompatActivity {
    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_data);

        String title = "Проверка данных";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        findViewById(R.id.b_next1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckData.this, ActivityAccountReady.class);
                startActivity(intent);
            }
        });
    }
}
