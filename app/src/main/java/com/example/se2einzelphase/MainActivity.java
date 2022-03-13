package com.example.se2einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // enable/permit network requests
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // handle button click to send request
        ((Button) findViewById(R.id.btn_send)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // execute request with payload & display response
                String response = new RequestTask().doInBackground("11941875\n");
                ((TextView) findViewById(R.id.txt_server_response)).setText(response);
            }
        });

    }

    private static class RequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Socket socket = new Socket("se2-isys.aau.at", 53212);
                DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // send request & get response
                writer.writeBytes(strings[0] + "\n");
                String response = reader.readLine();

                // close stuff
                for (Closeable toclose: new Closeable[]{socket, writer, reader}) toclose.close();

                return response;

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
    }
}