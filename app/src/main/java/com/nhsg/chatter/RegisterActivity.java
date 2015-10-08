package com.nhsg.chatter;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_register);

        Button registerBtn = (Button) findViewById(R.id.register_button);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username_reg)).getText().toString();
                String password = ((EditText) findViewById(R.id.password_reg)).getText().toString();
                String password_confirm = ((EditText) findViewById(R.id.password_confirm_reg)).getText().toString();

                if( !password.equals(password_confirm) ) {
                    EditText confirmET = (EditText) findViewById(R.id.password_confirm_reg);
                    confirmET.setError("Passwords must match.");
                }

                String email = ((EditText) findViewById(R.id.email_reg)).getText().toString();
                // TODO: send registration request to server
                String urlString = "http://messengerproject-dev.elasticbeanstalk.com/messaging/signup/";
//                urlString = "http://www.google.ca/";
                try {
                    // Create parameters JSONObject
//                    String[] jsonArray = new String[] { "abc", "sdsf", "sfsd" };
                    JSONObject parameters = new JSONObject();
//                    parameters.put("foldername", "imageFolder");
//                    parameters.put("jsonArray", new JSONArray(Arrays.asList(jsonArray)));
//                    parameters.put("location", "Dhaka");
                    parameters.put("username", username);
                    parameters.put("password", password);
                    parameters.put("email", email);

                    // Open connection to URL and perform POST request.
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");

                    connection.setRequestProperty("Content-Length", "" +
                            Integer.toString(parameters.toString().getBytes().length));
                    connection.setRequestProperty("Content-Language", "en-US");

                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    // Write serialized JSON data to output stream.
                    connection.setReadTimeout(4000);
                    connection.setConnectTimeout(5000);
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(parameters.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    connection.connect();

                    // Close streams and disconnect.
                    connection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
