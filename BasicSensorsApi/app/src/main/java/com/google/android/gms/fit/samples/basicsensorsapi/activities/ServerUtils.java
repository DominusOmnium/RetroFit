package com.google.android.gms.fit.samples.basicsensorsapi.activities;

import android.preference.PreferenceManager;
import android.util.Base64;

import com.google.android.gms.fit.samples.basicsensorsapi.AsyncRequest;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerUtils {

    public static final String address = "https://test-api.mosmedzdrav.ru/zabota/api/sensordata";
    public static final String addresspress = "https://test-api.mosmedzdrav.ru/zabota/api/sensordata/batch";
    public static final int TYPE_PRESSURE_1 = 1;
    public static final int TYPE_PRESSURE_2 = 2;
    public static final int TYPE_WEIGHT = 3;
    public static final int TYPE_STEPS = 4;
    public static final int TYPE_HEART_RATE = 5;

    public static JSONObject buidJsonObjectAuth(String oms, String date) throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException {

        String value = oms.replace(" ", "") + date;

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = df.format(c);

        String check_val = value + formattedDate + "Zabota+";

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(check_val.getBytes("UTF-8"));
        byte[] cv = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : cv) {
            sb.append(String.format("%02x", b));
        }
        String ChallengeVerification = sb.toString();

        byte[] data = value.getBytes("UTF-8");
        String Base64Value = Base64.encodeToString(data, Base64.NO_WRAP);

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("Challenge", Base64Value + "." + ChallengeVerification);

        return jsonObject;
    }

    public static JSONObject buidJsonObject(List<Bucket> buckets, int Type) throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException {
        JSONArray arr = new JSONArray();
        int i = 0;
        for (Bucket bucket : buckets) {
            JSONObject json = ServerUtils.buidJsonObjectFromDataSets(bucket.getDataSets(), Type);
            if (json.length() != 0) {
                arr.put(i, json);
                i++;
            }
        }

        JSONObject fo = new JSONObject();
        fo.put("collection", arr);
        return fo;
    }

    public static JSONObject buidJsonObjectFromDataSets(List<DataSet> dataSets, int Type) throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException {
        JSONObject j = new JSONObject();
        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataPoints().size() == 0) {
                continue;
            }

            for (DataPoint dp : dataSet.getDataPoints()) {
                for (Field field : dp.getDataType().getFields()) {
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(dp.getStartTime(TimeUnit.SECONDS) * 1000);
                        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                        j.put("Value", dp.getValue(field).asInt());
                        j.put("Type", Type);
                        j.put("Date", df.format(calendar.getTime()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return j;
    }

    public static JSONObject buidArrJsonObjectFromDataSets(List<DataSet> dataSets, int Type) throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException {
        JSONArray arr = new JSONArray();
        int i = 0;
        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataPoints().size() == 0) {
                continue;
            }

            for (DataPoint dp : dataSet.getDataPoints()) {
                for (Field field : dp.getDataType().getFields()) {
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(dp.getStartTime(TimeUnit.SECONDS) * 1000);
                        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                        JSONObject j = new JSONObject();
                        if (Type == TYPE_PRESSURE_1)
                        {
                            if (field.getName().equals("blood_pressure_systolic")) {
                                j.put("Value", dp.getValue(field).asFloat());
                                j.put("Type", TYPE_PRESSURE_1);
                                j.put("Date", df.format(calendar.getTime()));
                                arr.put(i, j);
                                i++;
                            } else if (field.getName().equals("blood_pressure_diastolic")) {
                                j.put("Value", dp.getValue(field).asFloat());
                                j.put("Type", TYPE_PRESSURE_2);
                                j.put("Date", df.format(calendar.getTime()));
                                arr.put(i, j);
                                i++;
                            }
                        }
                        else {
                            j.put("Value", dp.getValue(field).asFloat());
                            j.put("Type", Type);
                            j.put("Date", df.format(calendar.getTime()));
                            arr.put(i, j);
                            i++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        JSONObject fo = new JSONObject();
        fo.put("collection", arr);
        return fo;
    }

    public static JSONObject buidJsonObject(float Value, int Type, String date) throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("Type", Type);
        jsonObject.accumulate("Value", Value);
        jsonObject.accumulate("Date", date);

        return jsonObject;
    }

    public static class BP {
        public float Value;
        public int Type;
        public String Date;

        public BP(float f, int i, String s)
        {
            Value = f;
            Type = i;
            Date = s;
        }
    }

    public static JSONObject buidJsonObject(float Value1, float Value2, String date) throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException {
        JSONArray arr = new JSONArray();
        JSONObject j = new JSONObject();
        JSONObject j2 = new JSONObject();
        j.put("Value", Value1);
        j.put("Type", TYPE_PRESSURE_1);
        j.put("Date", date);
        arr.put(0, j);
        j2.put("Value", Value2);
        j2.put("Type", TYPE_PRESSURE_2);
        j2.put("Date", date);
        arr.put(1, j2);
        JSONObject fo = new JSONObject();
        fo.put("collection", arr);

        return fo;
    }
}
