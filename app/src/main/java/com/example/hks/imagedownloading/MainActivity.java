package com.example.hks.imagedownloading;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private EditText editText;
    ListView listView;
    private Button button;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private String[] listofImages;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listofImages = getResources().getStringArray(R.array.imageUrls);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        handler = new Handler();
    }

    public void downloadimage(View view) {
        String url = editText.getText().toString();
        Thread myThread = new Thread(new DownloadImagesThread(url));
        myThread.start();
    }

    public boolean downloadimageThread(String url) {
       /*
       1.create url object that represents the url
       2.open connection using url object
       3.read data using inputstream into byte array
       4.open file outputstream to save data on sdcard
       5.write data to the file outputstream
       6.close all connections
        */

        boolean successful = false;
        URL downloadURL = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        File file = null;

        try {
            downloadURL = new URL(url);
            connection = (HttpURLConnection) downloadURL.openConnection();
            inputStream = connection.getInputStream();
//            file = new File(Environment
//                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                    .getAbsolutePath() + "/" + Uri.parse(url).getLastPathSegment());
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    Uri.parse(url).getLastPathSegment());
            file.createNewFile();

            fileOutputStream = new FileOutputStream(file);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
            successful = true;

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setVisibility(View.GONE);
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            });

            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }


        return successful;

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        editText.setText(listofImages[position]);
    }

    private class DownloadImagesThread implements Runnable {

        private String url;

        public DownloadImagesThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setVisibility(View.VISIBLE);
                }
            });
            downloadimageThread(url);
        }
    }
}
