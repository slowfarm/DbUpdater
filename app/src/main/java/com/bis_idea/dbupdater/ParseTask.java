package com.bis_idea.dbupdater;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by u102 on 31.10.2016.
 */

class ParseTask extends AsyncTask<Void, Void, String> {
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";
    String request = "http://3-david.bis-idea.ru/api/gettabledata.php";
    URL url = null;

    @Override
    protected String doInBackground(Void... params) {
        try {
            url = new URL(request);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            resultJson = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);
    }
}