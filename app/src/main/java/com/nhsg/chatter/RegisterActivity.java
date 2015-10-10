package com.nhsg.chatter;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_register);

        Bundle extras = getIntent().getExtras();
        String extraName = extras.getString("name");
        String extraPassword = extras.getString("password");
        if( !extraName.isEmpty() )
            ((EditText) findViewById(R.id.username_reg)).setText(extraName);
        if( !extraPassword.isEmpty() )
            ((EditText) findViewById(R.id.password_reg)).setText(extraPassword);

        Button registerBtn = (Button) findViewById(R.id.register_button);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username_reg)).getText().toString();
                String password = ((EditText) findViewById(R.id.password_reg)).getText().toString();
                String password_confirm = ((EditText) findViewById(R.id.password_confirm_reg)).getText().toString();
                String email = ((EditText) findViewById(R.id.email_reg)).getText().toString();

                if( !password.equals(password_confirm) ) {
                    EditText confirmET = (EditText) findViewById(R.id.password_confirm_reg);
                    confirmET.setError("Passwords must match.");
                }

                if( !isEmailValid(email)) {
                    EditText emailET = (EditText) findViewById(R.id.email_reg);
                    emailET.setError("Please input a valid email address");
                }

                // TODO: send registration request to server

                String urlString = "http://messengerproject-dev.elasticbeanstalk.com/messaging/signup/";
                new SignupPostTask().execute(urlString, username, password, email);
            }
        });
    }


    private class SignupPostTask extends AsyncTask<String, Void, Void> {
        // onPostExecute displays the results of the AsyncTask.


        @Override
        protected Void doInBackground(String... params) {

            String urlString = params[0];
            try {
                // Create parameters JSONObject
                JSONObject parameters = new JSONObject();
//                    parameters.put("foldername", "imageFolder");
//                    parameters.put("jsonArray", new JSONArray(Arrays.asList(jsonArray)));
//                    parameters.put("location", "Dhaka");
                parameters.put("username", params[1]);
                parameters.put("password", params[2]);
                parameters.put("email", params[3]);

                // Open connection to URL and perform POST request.
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(parameters.toString().getBytes().length);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/json");
//                connection.setHeader("Content-type", "application/json");

//                connection.setRequestProperty("Content-Length", "" +
//                        Integer.toString(parameters.toString().getBytes().length));
//                connection.setRequestProperty("Content-Language", "en-US");

                connection.setDoInput(true);

                // Write serialized JSON data to output stream.
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.write(parameters.toString().getBytes());

                int response_code = connection.getResponseCode();
                String response_msg = connection.getResponseMessage().toString();

//                Map<String, List<String>> response = connection.getRequestProperties();

//                String utf8 = connection.getContentEncoding();

                InputStream in = connection.getInputStream();
                String encoding = connection.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String body = IOUtils.toString(in, encoding);

                // Close streams and disconnect.
                connection.disconnect();
                os.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return null;
        }
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

    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
