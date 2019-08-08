package com.google.android.gms.fit.samples.basicsensorsapi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;

import com.google.android.gms.fit.samples.basicsensorsapi.R;

public class Sources extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sources);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view_s);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String title = "Источники";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.profile:
                    startActivity(new Intent(Sources.this, profile.class));
                    return true;
                case R.id.data:
                    startActivity(new Intent(Sources.this, Sources.class));
                    return true;
                case R.id.sources:
                    startActivity(new Intent(Sources.this, Profile1.class));
                    return true;
            }

            return true;
        }
    };
}
