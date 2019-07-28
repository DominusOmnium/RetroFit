package com.google.android.gms.fit.samples.basicsensorsapi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class AsynkTaskRequests extends AsyncTask<String, Integer, Integer> {

    TextView tvIsConnected;
    Context context;
    EditText etTwitter;
    EditText etCountry;
    EditText etName;
    TextView tvResult;

    public  AsynkTaskRequests(TextView tv1, TextView tv2, EditText et1, EditText et2, EditText et3, Context c) {
        tvIsConnected = tv1;
        tvResult = tv2;
        etName = et1;
        etCountry = et2;
        etTwitter = et3;
        context = c;
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            //tvIsConnected.setText("Connected "+networkInfo.getTypeName());
            // change background color to red
            //tvIsConnected.setBackgroundColor(0xFF7CCC26);


        } else {
            // show "Not Connected"
            //tvIsConnected.setText("Not Connected");
            // change background color to green
            //tvIsConnected.setBackgroundColor(0xFFFF0000);
        }

        if (isConnected)
            Log.i("123","CONECTION OK");
        else
            Log.i("123","CONECTION NOT OK");

        return isConnected;
    }

//    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {
//
//        OutputStream os = conn.getOutputStream();
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//        writer.write(jsonObject.toString());
//        Log.i(MainActivity.class.toString(), jsonObject.toString());
//        writer.flush();
//        writer.close();
//        os.close();
//    }


    public String Notife(String str)
    {
//      print result
//        //Log.d(TAG,"Response string: " + response.toString());
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        // до версии Android 8.0 API 26
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentIntent(contentIntent)
                // обязательные настройки
                .setSmallIcon(R.drawable.logo)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("ВНИМАНИЕ!!!")
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
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Альтернативный вариант
        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(101, builder.build());

        //return response.toString();
        return "";
    }


    private String httpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        conn.setRequestProperty("Authorization:", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiMTIzNDU2Nzg5MCIsIm5hbWUiOiJDaGlsZCIsImlhdCI6MTUxNjIzOTAyMn0.WWS9CvvPLv94pqfbzDjXRrAic6YiTV4bdwGBJcPU7y4");



        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message

        BufferedReader reader=null;
        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder buf=new StringBuilder();
        String line=null;
        while ((line=reader.readLine()) != null) {
            buf.append(line + "\n");}
        Notife(buf.toString());
        return conn.getResponseMessage()+"";

    }

    @Override
    protected Integer doInBackground(String... urls) {
        while (true) { // стартуем бесконечный цикл
            Log.i("123","000000000000000000000000000000000000000");


                try {
                    Log.i("123","777777777777777777777777777777777777777777777");
                    httpPost(urls[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }


            Log.i("123","1111111111111111111111111111111111");
            try {
                Log.i("123","2222222222222222222222222222222");
                Thread.sleep(5000);
                Log.i("123","33333333333333333333333333333333333333");
            } catch (Exception e) {

                Log.i("123","44444444444444444444444444444444444444444444444444444");
                Log.i("chat",
                        "+ FoneService - ошибка процесса: "
                                + e.getMessage());

                return null;
            }
        }
    }
}
