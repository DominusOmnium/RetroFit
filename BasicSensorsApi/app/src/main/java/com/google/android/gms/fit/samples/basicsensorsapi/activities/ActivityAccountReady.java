package com.google.android.gms.fit.samples.basicsensorsapi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.fit.samples.basicsensorsapi.DiaryActivity;
import com.google.android.gms.fit.samples.basicsensorsapi.MainActivity;
import com.google.android.gms.fit.samples.basicsensorsapi.R;

public class ActivityAccountReady extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_ready);

        String title = "Готово";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        Button b_inApp = findViewById(R.id.b_inApp);
        b_inApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityAccountReady.this, DiaryActivity.class);
                startActivity(intent);
            }
        });
    }
}
