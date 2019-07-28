package com.google.android.gms.fit.samples.basicsensorsapi;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPAsyncTask extends AsyncTask<String, Void, String> {


    EditText etName;
    EditText etCountry;
    EditText etTwitter;
    TextView tvResult;

    public HTTPAsyncTask(EditText et1, EditText et2, EditText et3, TextView tv1)
    {
        etName = et1;
        etCountry = et2;
        etTwitter = et3;
        tvResult = tv1;
    }

    public HTTPAsyncTask()
    {
        etName = null;
        etCountry = null;
        etTwitter = null;
        tvResult = null;
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        Log.i("123","8888888888888888888888888888888888888888");
        OutputStream os = conn.getOutputStream();
        Log.i("123","123123123123");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        Log.i("123","000000000000000000");
        writer.write(jsonObject.toString());
        Log.i("123","111111111111111");
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        Log.i("123","22222222222222222");
        writer.flush();
        Log.i("123","333333333333333333333");
        writer.close();
        Log.i("123","44444444444444444444444444");
        os.close();
        Log.i("123","9999999999999999999999999999999999999");
    }

    private JSONObject buidJsonObject() throws JSONException {

        Log.i("123","bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("isNotify", "true");
        jsonObject.accumulate("Id",  0);
        jsonObject.accumulate("UserId",  1);
        jsonObject.accumulate("Type",  5);
        jsonObject.accumulate("Value",  49);
        Log.i("123","hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");

        return jsonObject;
    }

    private String httpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        Log.i("123","22222222222222222222222222222222222222");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        conn.setRequestProperty("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiMTIzNDU2Nzg5MCIsIm5hbWUiOiJDaGlsZCIsImlhdCI6MTUxNjIzOTAyMn0.WWS9CvvPLv94pqfbzDjXRrAic6YiTV4bdwGBJcPU7y4");

        // 2. build JSON object
        Log.i("123","333333333333333333333333333333333333333");
        JSONObject jsonObject = buidJsonObject();

            // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);


        // 4. make POST request to the given URL
        Log.i("123","44444444444444444444444444444444444444444444444");
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

    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.

        Log.i("123","000000000000000000000000000000000000000");
        try {
            try {
                Log.i("123","1111111111111111111111111111111111111");
                return httpPost(urls[0]);
            } catch (JSONException e) {

                Log.i("123","dddddddddddddddddddddddddddddd");
                e.printStackTrace();
                return "Error!";
            }
        } catch (IOException e) {

            Log.i("123","sssssssssssssssssssssssssssssssssssssssss");
            Log.i("123", e.getMessage());
            return e.getMessage();
        }
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        //tvResult.setText(result);
    }
}
