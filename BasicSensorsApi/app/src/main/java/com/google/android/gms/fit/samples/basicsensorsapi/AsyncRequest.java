package com.google.android.gms.fit.samples.basicsensorsapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.google.android.gms.fit.samples.basicsensorsapi.activities.StartActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

public class AsyncRequest extends AsyncTask<String, Integer, Integer> {

    static Context context;
    static String adress;
    static JSONObject data;

    public AsyncRequest(Context c, String addr, JSONObject d)
    {
        context = c;
        adress = addr;
        data = d;
    }


    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }


    public static class ClientKeyStoresTrustManager implements X509TrustManager {

        protected ArrayList<X509TrustManager> x509TrustManagers = new ArrayList<X509TrustManager>();

        protected ClientKeyStoresTrustManager() {
            final ArrayList<TrustManagerFactory> factories = new ArrayList<TrustManagerFactory>();

            try {
                final TrustManagerFactory original = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                original.init((KeyStore) null);
                factories.add(original);
                KeyStore keyStore = KeyStore.getInstance( "PKCS12" );
                InputStream is = context.getResources().openRawResource(R.raw.p12);
                keyStore.load( is, "p123456P".toCharArray() );
                final TrustManagerFactory additionalCerts = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                additionalCerts.init(keyStore);
                factories.add(additionalCerts);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            for (TrustManagerFactory tmf : factories)
                for ( TrustManager tm : tmf.getTrustManagers() )
                    if (tm instanceof X509TrustManager)
                        x509TrustManagers.add( (X509TrustManager) tm );
            if ( x509TrustManagers.size() == 0 )
                throw new RuntimeException("Couldn't find any X509TrustManagers");

        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        public X509Certificate[] getAcceptedIssuers() {
            final ArrayList<X509Certificate> list = new ArrayList<X509Certificate>();
            for ( X509TrustManager tm : x509TrustManagers )
                list.addAll(Arrays.asList(tm.getAcceptedIssuers()));
            return list.toArray(new X509Certificate[list.size()]);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try {
            KeyManagerFactory km = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance( "PKCS12" );
            InputStream is = context.getResources().openRawResource(R.raw.p12);
            keyStore.load( is, "p123456P".toCharArray() );
            km.init(keyStore, null);
            SSLContext sslc = SSLContext.getInstance("TLS");
            sslc.init(km.getKeyManagers(), new TrustManager[]{new ClientKeyStoresTrustManager()},
                    new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Integer doInBackground(String... strings) {

        //GET URL
        URL url = null;
        try {
            url = new URL(adress);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        //START CONNECTION
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url
                    .openConnection();
            connection.setRequestMethod("POST");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        connection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        //REQUEST
        try {
            connection.setRequestProperty("Content-type", "application/json");
            setPostRequestContent(connection, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //RESPONSE
        int responseCode = 0;
        try {
            responseCode = connection.getResponseCode();

            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder buf = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line + "\n");}
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            pref.edit().putString("auth", buf.toString()).commit();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            return 1;
        }
        return 0;
    }
}
