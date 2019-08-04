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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.fit.samples.basicsensorsapi.R;

public class CreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        String title = "Создать аккаунт";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        final TextView b_next = findViewById(R.id.b_next);
        final Button b_signup = findViewById(R.id.b_signup);

        ((CheckBox)findViewById(R.id.cb_access)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    b_next.setTextColor(getResources().getColor(R.color.white));
                    b_signup.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    b_next.setTextColor(getResources().getColor(R.color.disabledtext));
                    b_signup.setTextColor(getResources().getColor(R.color.disabledtext));
                }
            }
        });

        View.OnClickListener click  =    new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (b_next.getCurrentTextColor() == getResources().getColor(R.color.white )) {
                    Intent intent = new Intent(CreateActivity.this, CheckData.class);
                    startActivity(intent);
                }
            }
        };
        b_next.setOnClickListener(click);
        b_signup.setOnClickListener(click);
    }
}
