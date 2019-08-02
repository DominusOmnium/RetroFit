package com.google.android.gms.fit.samples.basicsensorsapi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class ServerActivity extends AppCompatActivity {

    TextView tvIsConnected;
    EditText etName;
    EditText etCountry;
    EditText etTwitter;
    TextView tvResult;

    boolean Post = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        etName = findViewById(R.id.etName);
        etCountry = findViewById(R.id.etCountry);
        etTwitter = findViewById(R.id.etTwitter);
        tvResult = (TextView) findViewById(R.id.tvResult);
        checkNetworkConnection();
        new AsynkTaskRequests(tvIsConnected, tvResult, etName, etCountry, etTwitter, this).execute("http://194.58.102.106/api/zmagic/parent");
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            tvIsConnected.setText("Connected "+networkInfo.getTypeName());
            tvIsConnected.setBackgroundColor(0xFF7CCC26);


        } else {
            tvIsConnected.setText("Not Connected");
            tvIsConnected.setBackgroundColor(0xFFFF0000);
        }

        return isConnected;
    }

    public void sendPost(View view) {
        Post = true;
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        if(checkNetworkConnection())
            new HTTPAsyncTask(etName, etCountry, etTwitter, tvResult).execute("http://194.58.102.106/api/zmagic");
        else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
}

    public String Notife(String str)
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Напоминание")
                .setContentText(str)
                .setTicker("Последнее китайское предупреждение!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101, builder.build());
        return "";
    }
}