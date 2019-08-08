package com.google.android.gms.fit.samples.basicsensorsapi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.DigitsKeyListener;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.fit.samples.basicsensorsapi.AsyncRequest;
import com.google.android.gms.fit.samples.basicsensorsapi.CheckData;
import com.google.android.gms.fit.samples.basicsensorsapi.R;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.google.android.gms.fit.samples.basicsensorsapi.activities.ServerUtils.buidJsonObjectAuth;

public class CreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        String title = "Регистрация";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        final EditText in_name = findViewById(R.id.input_name);
        final EditText in_date = findViewById(R.id.input_date);
        final EditText in_oms = findViewById(R.id.input_oms);
        final Button b_signup = findViewById(R.id.b_signup);

        ((CheckBox)findViewById(R.id.cb_access)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    b_signup.setTextColor(getResources().getColor(R.color.white));
                else
                    b_signup.setTextColor(getResources().getColor(R.color.disabledtext));
            }
        });

        b_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (b_signup.getCurrentTextColor() == getResources().getColor(R.color.disabledtext))
                    return;
                if (in_date.getText().toString().length() != 10 ||
                        in_oms.getText().toString().length() != 19 ||
                        (in_name.getText().toString().split(" ").length != 3))
                {
                    findViewById(R.id.ll_error).setVisibility(View.VISIBLE);
                }
                else {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("FIO", in_name.getText().toString()).commit();
                    findViewById(R.id.ll_error).setVisibility(View.INVISIBLE);
                    JSONObject data = null;
                    try {
                        data = buidJsonObjectAuth(in_oms.getText().toString(), in_date.getText().toString());
                        if (data != null)
                            new AsyncRequest(getApplicationContext(), "https://test-api.mosmedzdrav.ru/zabota/api/register", data).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(CreateActivity.this, ActivityAccountReady.class);
                    startActivity(intent);
                }
            }
        });

        View.OnFocusChangeListener focuschange = new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    ((RelativeLayout)view.getParent()).setTranslationZ(5);
                }
                else
                {
                    ((RelativeLayout)view.getParent()).setTranslationZ(0);
                }
            }
        };

        in_date.setInputType(InputType.TYPE_CLASS_NUMBER);
        in_date.setKeyListener(DigitsKeyListener.getInstance("1234567890."));
        in_date.addTextChangedListener(new MaskedTextChangedListener("[00].[00].[0000]", in_date));

        in_oms.setInputType(InputType.TYPE_CLASS_NUMBER);
        in_oms.setKeyListener(DigitsKeyListener.getInstance("1234567890 "));
        in_oms.addTextChangedListener(new MaskedTextChangedListener("[0000] [0000] [0000] [0000]", in_oms));

        in_name.setOnFocusChangeListener(focuschange);
        in_date.setOnFocusChangeListener(focuschange);
        in_oms.setOnFocusChangeListener(focuschange);
    }



}
