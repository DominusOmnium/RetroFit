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
        //startLoop();


    }

    // check network connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            tvIsConnected.setText("Connected "+networkInfo.getTypeName());
            // change background color to red
            tvIsConnected.setBackgroundColor(0xFF7CCC26);


        } else {
            // show "Not Connected"
            tvIsConnected.setText("Not Connected");
            // change background color to green
            tvIsConnected.setBackgroundColor(0xFFFF0000);
        }

        return isConnected;
    }

/*
    private String httpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (Post)
            conn.setRequestMethod("POST");
        else
            conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");


        if (Post)
        {
            // 2. build JSON object
            JSONObject jsonObject = buidJsonObject();

            // 3. add JSON content to POST request body
            setPostRequestContent(conn, jsonObject);
        }


        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message

//        BufferedReader reader=null;
//        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        StringBuilder buf=new StringBuilder();
//        String line=null;
//        while ((line=reader.readLine()) != null) {
//            buf.append(line + "\n");}
//        Notife(buf.toString());
        return conn.getResponseMessage()+"";

    }


    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return httpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            tvResult.setText(result);
        }
    }


    public void sendPost(View view) {
        Post = true;
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        // perform HTTP POST request
        if(checkNetworkConnection())
            new HTTPAsyncTask().execute("http://194.58.102.106/api/zmagic");
        else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

    }

    public void sendGet() {
        Post = false;
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        // perform HTTP POST request
        if(checkNetworkConnection())
            new HTTPAsyncTask().execute("http://194.58.102.106/api/zmagic/parent");
    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("name", etName.getText().toString());
        jsonObject.accumulate("country",  etCountry.getText().toString());
        jsonObject.accumulate("twitter",  etTwitter.getText().toString());

        return jsonObject;
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

    private void startLoop()
    {
        Thread thr = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                { // стартуем бесконечный цикл

                    sendGet();

                    try {
                        Thread.sleep(15000);
                    } catch (Exception e) {
                        Log.i("chat",
                                "+ FoneService - ошибка процесса: "
                                        + e.getMessage());
                    }
                }
            }
        });

        thr.setDaemon(true);
        thr.start();
    }

*/
    public void sendPost(View view) {
        Post = true;
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        // perform HTTP POST request
        if(checkNetworkConnection())
            new HTTPAsyncTask(etName, etCountry, etTwitter, tvResult).execute("http://194.58.102.106/api/zmagic");
        else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

}

    public String Notife(String str)
    {
//      print result
//        //Log.d(TAG,"Response string: " + response.toString());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();

        // до версии Android 8.0 API 26
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(contentIntent)
                // обязательные настройки
                .setSmallIcon(R.drawable.logo)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Напоминание")
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(str) // Текст уведомления
                // необязательные настройки
               // .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher_background)) // большая
                // картинка
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("Последнее китайское предупреждение!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true); // автоматически закрыть уведомление после нажатия

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Альтернативный вариант
        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(101, builder.build());

        //return response.toString();
        return "";
    }
}