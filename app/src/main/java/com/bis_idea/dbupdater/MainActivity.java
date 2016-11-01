package com.bis_idea.dbupdater;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

    EditText editText;
    EditText editText2;
    Button button;
    Button button1;
    Toast toast;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FragmentAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DataBase mDatabase;
    private SQLiteDatabase mSqLiteDatabase;
    private ArrayList<String[]> label = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toast = Toast.makeText(this, "complete", Toast.LENGTH_SHORT);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);

        button = (Button) findViewById(R.id.button);
        button1 = (Button) findViewById(R.id.button2);

        button.setOnClickListener(this);
        button1.setOnClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        try { // проверка на наличие списка в БД
            if(getDatabase() == null) {
                String text = new ParseTask().execute().get();
                setDatabase(text);
                parser(getDatabase());
            }
            parser(getDatabase());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        setAdapter(this);
    }

    @Override
    public void onClick(View v) {
        String jsonStr = null;
        switch (v.getId()){
            case R.id.button:
                try {
                    JSONArray jsonArrayMessages = new JSONArray();
                    JSONObject jsonMessage = new JSONObject();
                    jsonMessage.put("label", editText.getText().toString().replace(" ", "+").replace("'","\\'"));
                    jsonMessage.put("author", editText2.getText().toString().replace(" ", "+").replace("'","\\'"));
                    jsonArrayMessages.put(jsonMessage);
                    jsonStr = jsonArrayMessages.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new POSTInsert(jsonStr, toast).execute();
                onRefresh();
                editText.setText("");
                editText2.setText("");
                break;
            case R.id.button2:
                new POSTClear(toast).execute();
                onRefresh();
                break;
        }
    }
    @Override
    public void onRefresh() {
        try { // Проверка наличия изменений и обновление списка
            String text = new ParseTask().execute().get();
            if(!getDatabase().equals(text) && text.length() != 0) {
                updateDatabase(text);
                parser(getDatabase());
                Toast toast = Toast.makeText(this,
                        "Список обновлен", Toast.LENGTH_SHORT);
                toast.show();
                setAdapter(this);
            }
            else {
                Toast toast = Toast.makeText(this,
                        "Список без изменения", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }
    // Добавление JSON строки в БД
    public void setDatabase(String strJson) {
        mDatabase = new DataBase(this, "database.db", null, 1);
        mSqLiteDatabase = mDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBase.MUSIC_COLUMN, strJson);
        mSqLiteDatabase.insert("music", null, values);
    }
    // Получение JSON строки из БД
    public String getDatabase() {
        String data = null;
        mDatabase = new DataBase(this, "database.db", null, 1);
        mSqLiteDatabase = mDatabase.getWritableDatabase();
        Cursor cursor = mSqLiteDatabase.query("music", new String[]{DataBase.MUSIC_COLUMN}, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor!=null && cursor.getCount() > 0)
            data = cursor.getString(cursor.getColumnIndex(DataBase.MUSIC_COLUMN));
        cursor.close();
        mSqLiteDatabase.close();
        return data;
    }
    // Обновление БД при изменении
    public void updateDatabase(String data) {
        mDatabase = new DataBase(this, "database.db", null, 1);
        mSqLiteDatabase = mDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBase.MUSIC_COLUMN, data);
        mSqLiteDatabase.update("music", values, null, null);
    }
    // JSON парсер
    public void parser(String strJson) {
        label.clear();
        try {
            JSONArray music = new JSONArray(strJson);
            for (int i = 0; i < music.length(); i++) {
                JSONObject song = music.getJSONObject(i);
                String[] fill = {song.getString("label"),song.getString("author")};
                label.add(fill);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // Установка адаптера
    public void setAdapter(Context ctx) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FragmentAdapter(label);
        mRecyclerView.setAdapter(mAdapter);
    }
}
