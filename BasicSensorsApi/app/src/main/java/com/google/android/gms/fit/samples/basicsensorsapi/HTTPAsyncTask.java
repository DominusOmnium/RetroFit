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

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("name", etName.getText().toString());
        jsonObject.accumulate("country",  etCountry.getText().toString());
        jsonObject.accumulate("twitter",  etTwitter.getText().toString());

        return jsonObject;
    }

    private String httpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        conn.setRequestProperty("Authorization:", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiMTIzNDU2Nzg5MCIsIm5hbWUiOiJQYXJlbnQiLCJpYXQiOjE1MTYyMzkwMjJ9.uOk3GDxGriOcxczL-Q1Z6EW7Tbs2vDXlMUEINSA64gk");

        // 2. build JSON object
            JSONObject jsonObject = buidJsonObject();

            // 3. add JSON content to POST request body
            setPostRequestContent(conn, jsonObject);


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
        //tvResult.setText(result);
    }
}
