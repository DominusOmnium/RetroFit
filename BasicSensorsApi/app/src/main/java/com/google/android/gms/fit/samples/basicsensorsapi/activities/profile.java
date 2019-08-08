package com.google.android.gms.fit.samples.basicsensorsapi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fit.samples.basicsensorsapi.AsyncRequest;
import com.google.android.gms.fit.samples.basicsensorsapi.R;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.common.logger.LogWrapper;
import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class profile extends AppCompatActivity
{

    public static final String TAG = "BasicHistoryApi";
    static TextView steps, steps_measure, steps_sent, weight, weight_sent, weight_measure, pressure, pressure_sent, pressure_measure, heartRate, heartRate_sent, heartRate_measure;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;


    static Boolean isSync = false;
    HistoryClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String title = "Все данные";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);


        //Steps
        steps = findViewById(R.id.steps);
        steps_measure = findViewById(R.id.steps_measure);
        steps_sent = findViewById(R.id.steps_sent);

        //Weight
        weight = findViewById(R.id.weight);
        weight_sent = findViewById(R.id.weight_sent);
        weight_measure = findViewById(R.id.weight_mesure);

        //Pressure
        pressure = findViewById(R.id.pressure);
        pressure_measure = findViewById(R.id.pressure_measure);
        pressure_sent = findViewById(R.id.pressure_sent);

        //Heart rate
        heartRate = findViewById(R.id.heartRate);
        heartRate_measure = findViewById(R.id.heartRate_measure);
        heartRate_sent = findViewById(R.id.heartRate_sent);

        initializeLogging();

        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                        .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.TYPE_HEART_RATE_BPM)
                        .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            client = Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this));
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            isSync = pref.getBoolean("sync", false);
            readData();
        }

        findViewById(R.id.b_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (client != null)
                    readData();
            }
        });
    }

    private void readData() {
        readSteps().addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
            @Override
            public void onComplete(@NonNull Task<DataReadResponse> task) {
                readWeight().addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<DataReadResponse> task) {
                        readPressure().addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<DataReadResponse> task) {
                                readHeartRate();
                            }
                        });
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "There was a problem reading the data.", e);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                readSteps();
            }
        }
    }

    public Task<DataReadResponse> readWeight() {
        DataReadRequest readRequest;
        if (isSync) {
            readRequest = new DataReadRequest.Builder ()
                    .setTimeRange(1, new Date().getTime(), TimeUnit.MILLISECONDS)
                    .setLimit(1)
                    .read(DataType.TYPE_WEIGHT)
                    .build();
        }
        else {
            readRequest = new DataReadRequest.Builder ()
                    .setTimeRange(1, new Date().getTime(), TimeUnit.MILLISECONDS)
                    .read(DataType.TYPE_WEIGHT)
                    .build();
        }

        return client.readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                if (isSync) {
                                    if (dataReadResponse.getBuckets().size() > 0) {
                                        for (Bucket bucket : dataReadResponse.getBuckets()) {
                                            List<DataSet> dataSets = bucket.getDataSets();
                                            for (DataSet dataSet : dataSets) {
                                                dumpDataSetWeight(dataSet);
                                            }
                                        }
                                    } else if (dataReadResponse.getDataSets().size() > 0) {
                                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                                            dumpDataSetWeight(dataSet);
                                        }
                                    }
                                } else {
                                    syncData(dataReadResponse, ServerUtils.TYPE_WEIGHT);
                                    PreferenceManager.getDefaultSharedPreferences(profile.this).edit().putBoolean("sync", true).commit();
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }

    public Task<DataReadResponse> readPressure() {
        DataReadRequest readRequest;

        if (isSync) {
            readRequest = new DataReadRequest.Builder ()
                    .setTimeRange(1, new Date().getTime(), TimeUnit.MILLISECONDS)
                    .setLimit(1)
                    .read (HealthDataTypes.TYPE_BLOOD_PRESSURE)
                    .build ();
        }
        else {
            readRequest = new DataReadRequest.Builder ()
                    .setTimeRange(1, new Date().getTime(), TimeUnit.MILLISECONDS)
                    .read (HealthDataTypes.TYPE_BLOOD_PRESSURE)
                    .build ();
        }

        return client.readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                if (isSync) {
                                if (dataReadResponse.getBuckets().size() > 0) {
                                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                                        List<DataSet> dataSets = bucket.getDataSets();
                                        for (DataSet dataSet : dataSets) {
                                            dumpDataSetPressure(dataSet);
                                        }
                                    }
                                } else if (dataReadResponse.getDataSets().size() > 0) {
                                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                                        dumpDataSetPressure(dataSet);
                                    }
                                }
                                } else {
                                    syncData(dataReadResponse, ServerUtils.TYPE_PRESSURE_1);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }

    private Task<DataReadResponse> readSteps() {
        DataReadRequest readRequest = queryStepData();

        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                if (isSync) {
                                    if (dataReadResponse.getBuckets().size() > 0) {
                                        for (Bucket bucket : dataReadResponse.getBuckets()) {
                                            List<DataSet> dataSets = bucket.getDataSets();
                                            for (DataSet dataSet : dataSets) {
                                                dumpDataSetSteps(dataSet);
                                            }
                                        }
                                    } else if (dataReadResponse.getDataSets().size() > 0) {
                                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                                            dumpDataSetSteps(dataSet);
                                        }
                                    }
                                }
                                else {
                                    syncStepsData(dataReadResponse);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                int i = 0;
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }

    private Task<DataReadResponse> readHeartRate() {
        DataReadRequest readRequest;

        if (isSync) {
            readRequest = new DataReadRequest.Builder ()
                    .setTimeRange(1, new Date().getTime(), TimeUnit.MILLISECONDS)
                    .setLimit(1)
                    .read (DataType.TYPE_HEART_RATE_BPM)
                    .build ();
        }
        else {
            readRequest = new DataReadRequest.Builder ()
                    .setTimeRange(1, new Date().getTime(), TimeUnit.MILLISECONDS)
                    .read (DataType.TYPE_HEART_RATE_BPM)
                    .build ();
        }

        return client.readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                if (isSync) {
                                    if (dataReadResponse.getBuckets().size() > 0) {
                                        for (Bucket bucket : dataReadResponse.getBuckets()) {
                                            List<DataSet> dataSets = bucket.getDataSets();
                                            for (DataSet dataSet : dataSets) {
                                                dumpDataSetHeartRate(dataSet);
                                            }
                                        }
                                    } else if (dataReadResponse.getDataSets().size() > 0) {
                                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                                            dumpDataSetHeartRate(dataSet);
                                        }
                                    }
                                } else {
                                    syncData(dataReadResponse, ServerUtils.TYPE_HEART_RATE);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }

    public static DataReadRequest queryStepData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        long startTime;
        if (!isSync) {
            cal.set(2014, 9, 28);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startTime = cal.getTimeInMillis();


        DataSource ds = new DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build();

        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(ds, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readRequest;
    }

    private void dumpDataSetSteps(DataSet dataSet) {

        if (dataSet.getDataPoints().size() == 0) {
            steps.setText("n/a");
            steps_measure.setText("n/a");
            steps_sent.setText("n/a");
            return;
        }

        for (DataPoint dp : dataSet.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                try {
                    steps.setText(String.valueOf(dp.getValue(field).asInt()));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dp.getStartTime(TimeUnit.SECONDS) * 1000);
                    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                    steps_measure.setText(df.format(calendar.getTime()));
                    steps_sent.setText(df.format(Calendar.getInstance().getTime()));
                } catch (Exception e) {
                    steps.setText("n/a");
                    steps_measure.setText("n/a");
                    steps_sent.setText("n/a");
                }
                JSONObject json = null;
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dp.getStartTime(TimeUnit.SECONDS) * 1000);
                    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                    json = ServerUtils.buidJsonObject(dp.getValue(field).asInt(), ServerUtils.TYPE_STEPS, df.format(calendar.getTime()));
                    new AsyncRequest(getApplicationContext(), ServerUtils.address, json, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("auth", null)).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void syncStepsData(DataReadResponse dataReadResponse) {
        JSONObject json = null;
        try {
            if (dataReadResponse.getBuckets().size() > 0) {
                json = ServerUtils.buidJsonObject(dataReadResponse.getBuckets(), 4);
            } else if (dataReadResponse.getDataSets().size() > 0) {
                json = ServerUtils.buidJsonObjectFromDataSets(dataReadResponse.getDataSets(), 4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (json.getJSONArray("collection").length() != 0)
                steps.setText(json.getJSONArray("collection").getJSONObject(json.getJSONArray("collection").length() - 1).getInt("Value"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new AsyncRequest(getApplicationContext(), ServerUtils.addresspress, json, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("auth", null)).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncData(DataReadResponse dataReadResponse, int Type) {
        JSONObject json = null;
        try {
            if (dataReadResponse.getDataSets().size() > 0) {
                json = ServerUtils.buidArrJsonObjectFromDataSets(dataReadResponse.getDataSets(), Type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new AsyncRequest(getApplicationContext(), ServerUtils.addresspress, json, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("auth", null)).execute().get();
            if (json.getJSONArray("collection").length() != 0) {
                if (Type == ServerUtils.TYPE_WEIGHT) {
                    NumberFormat formatter = new DecimalFormat("#0.0");
                    weight.setText(formatter.format(json.getJSONArray("collection").getJSONObject(json.getJSONArray("collection").length() - 1).getDouble("Value")));
                } else if (Type == ServerUtils.TYPE_PRESSURE_1)
                    pressure.setText((int)json.getJSONArray("collection").getJSONObject(json.getJSONArray("collection").length() - 1).getDouble("Value") + "/" + (int)json.getJSONArray("collection").getJSONObject(json.getJSONArray("collection").length() - 2).getDouble("Value"));
                else if (Type == ServerUtils.TYPE_HEART_RATE)
                    heartRate.setText(String.valueOf(json.getJSONArray("collection").getJSONObject(json.getJSONArray("collection").length() - 1).getInt("Value")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dumpDataSetWeight(DataSet dataSet) {
        if (dataSet.getDataPoints().size() == 0)
        {
            weight.setText("n/a");
            weight_measure.setText("n/a");
            weight_sent.setText("n/a");
            return;
        }
        DataPoint dp = dataSet.getDataPoints().get(dataSet.getDataPoints().size() - 1);

        for (Field field : dp.getDataType().getFields()) {
            try {
                weight.setText(String.valueOf(dp.getValue(field).asFloat()));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dp.getTimestamp(TimeUnit.SECONDS) * 1000);
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                weight_measure.setText(df.format(calendar.getTime()));
                weight_sent.setText(df.format(Calendar.getInstance().getTime()));
            } catch (Exception e) {
                weight.setText("n/a");
                weight_measure.setText("n/a");
                weight_sent.setText("n/a");
            }
            JSONObject json = null;
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dp.getTimestamp(TimeUnit.SECONDS) * 1000);
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                json = ServerUtils.buidJsonObject(dp.getValue(field).asFloat(), ServerUtils.TYPE_WEIGHT, df.format(calendar.getTime()));
                new AsyncRequest(getApplicationContext(), ServerUtils.address, json, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("auth", null)).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dumpDataSetHeartRate(DataSet dataSet) {
        if (dataSet.getDataPoints().size() == 0) {
            heartRate.setText("n/a");
            heartRate_sent.setText("n/a");
            heartRate_measure.setText("n/a");
            return;
        }
        DataPoint dp = dataSet.getDataPoints().get(dataSet.getDataPoints().size() - 1);

        for (Field field : dp.getDataType().getFields()) {
            try {
                heartRate.setText(String.valueOf(dp.getValue(field).asFloat()));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dp.getTimestamp(TimeUnit.SECONDS) * 1000);
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                heartRate_measure.setText(df.format(calendar.getTime()));
                heartRate_sent.setText(df.format(Calendar.getInstance().getTime()));
            } catch (Exception e) {
                heartRate.setText("n/a");
                heartRate_sent.setText("n/a");
                heartRate_measure.setText("n/a");
            }
            JSONObject json = null;
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dp.getTimestamp(TimeUnit.SECONDS) * 1000);
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                json = ServerUtils.buidJsonObject(dp.getValue(field).asFloat(), ServerUtils.TYPE_HEART_RATE, df.format(calendar.getTime()));
                new AsyncRequest(getApplicationContext(), ServerUtils.address, json, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("auth", null)).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dumpDataSetPressure(DataSet dataSet) {
        if (dataSet.getDataPoints().size() == 0)
        {
            pressure.setText("n/a");
            pressure_measure.setText("n/a");
            pressure_sent.setText("n/a");
            return;
        }
        float f1 = 0;
        float f2 = 0;
        DataPoint dp = dataSet.getDataPoints().get(dataSet.getDataPoints().size() - 1);
        try {
            for (Field field : dp.getDataType().getFields()) {

                if (field.getName().equals("blood_pressure_systolic")) {
                    f1 = dp.getValue(field).asFloat();
                } else if (field.getName().equals("blood_pressure_diastolic")) {
                    f2 = dp.getValue(field).asFloat();
                }
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dp.getTimestamp(TimeUnit.SECONDS) * 1000);
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            pressure_measure.setText(df.format(calendar.getTime()));
            pressure_sent.setText(df.format(Calendar.getInstance().getTime()));
            pressure.setText(String.valueOf((int)f1) + "/" + String.valueOf((int)f2));
        } catch (Exception e){
            pressure.setText("n/a");
            pressure_measure.setText("n/a");
            pressure_sent.setText("n/a");
        }
        JSONObject json = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dp.getTimestamp(TimeUnit.SECONDS) * 1000);
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            json = ServerUtils.buidJsonObject(f1, f2, df.format(calendar.getTime()));
            new AsyncRequest(getApplicationContext(), ServerUtils.addresspress, json, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("auth", null), 1).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            Class fragmentClass = null;

            switch (item.getItemId())
            {
                case R.id.profile:
                    fragmentClass = profile.class;
                    return true;
                case R.id.data:

                    return true;
                case R.id.sources:

                    return true;
            }

            try
            {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            item.setChecked(true);
            setTitle(item.getTitle());

            return true;
        }
    };

}

