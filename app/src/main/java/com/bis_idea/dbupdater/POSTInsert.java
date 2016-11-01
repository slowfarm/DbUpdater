package com.bis_idea.dbupdater;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by u102 on 31.10.2016.
 */

class POSTInsert extends AsyncTask<Void, Void, Void> {

    String request = "http://3-david.bis-idea.ru/api/settabledata.php";
    String key = "?json=";
    HttpURLConnection conn;
    URL url = null;
    String jsonStr = null;
    Toast toast = null;

    public POSTInsert(String jsonStr, Toast toast) {
        this.jsonStr = jsonStr;
        this.toast = toast;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try{
            url = new URL(request + key + jsonStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));
            if ((reader.readLine()) != null) { toast.show(); }
            reader.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}