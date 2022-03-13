package com.example.se2einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

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
                String content = ((EditText) findViewById(R.id.txt_input)).getText().toString();
                String response = new RequestTask().doInBackground(content + "\n");
                ((TextView) findViewById(R.id.txt_server_response)).setText(response);
            }
        });

        // handle button click to sort
        ((Button) findViewById(R.id.btn_sort)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String content = ((EditText) findViewById(R.id.txt_input)).getText().toString();
                ((TextView) findViewById(R.id.txt_server_response)).setText(sort(content));
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
                for (Closeable toclose : new Closeable[]{socket, writer, reader}) toclose.close();

                return response;

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
    }


    /**
     * 11941875 % 7 = 1 (1. Aufgabe)
     * <p>
     * Matrikelnummer sortieren, wobei zuerst alle geraden,
     * dann alle ungeraden Ziffern gereiht sind (erst die geraden,
     * dann alle ungeraden Ziffern aufsteigend sortiert)
     */
    private String sort(String content) {
        try {
            // convert to array with single integer digits
            char[] chars = content.toCharArray();
            int[] digits = new int[chars.length];
            int n = digits.length;

            for (int i = 0; i < n; i++)
                digits[i] = Character.getNumericValue(chars[i]);

            // sort digits in ascending order (bubbleSort)
            for (int i = 0; i < n - 1; i++)
                for (int j = 0; j < n - i - 1; j++)
                    if (digits[j] > digits[j + 1]) {
                        // swap arr[j+1] and arr[j]
                        int temp = digits[j];
                        digits[j] = digits[j + 1];
                        digits[j + 1] = temp;
                    }

            // separate arrays for even & odd digits
            int evenCount = 0;
            for (int digit : digits) if (digit % 2 == 0) evenCount++;
            int oddCount = n - evenCount;

            int[] even = new int[evenCount];
            int[] odd = new int[oddCount];

            for (int i = n - 1; i >= 0; i--) {
                if (digits[i] % 2 == 0) {
                    even[--evenCount] = digits[i];
                } else {
                    odd[--oddCount] = digits[i];
                }
            }
            // merge even & odd numbers
            int[] result = new int[n];
            for (int i = 0; i < n; i++)
                result[i] = (i < even.length) ? even[i] : odd[i - even.length];

            // convert back to string
            return Arrays.toString(result);

        } catch (Exception e) {
            return e.getMessage();
        }
    }
}