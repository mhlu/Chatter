package com.nhsg.chatter;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

interface Callback {
    void call(Object... params);
}

public class PostTask extends AsyncTask<JSONObject, Void, Void> {
    // onPostExecute displays the results of the AsyncTask.
    private String urlString;
    public Callback callback = null;
    public PostTask(String urlString) {
        this.urlString = urlString;
    }
    @Override
    protected Void doInBackground(JSONObject... params) {


        try {
            // Create parameters JSONObject
            JSONObject parameters = params[0];

            // Open connection to URL and perform POST request.
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(parameters.toString().getBytes().length);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/json");

            connection.setDoInput(true);

            // Write serialized JSON data to output stream.
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.write(parameters.toString().getBytes());

            int response_code = connection.getResponseCode();
            String response_msg = connection.getResponseMessage().toString();

            InputStream in = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(in, encoding);
            JSONObject response = new JSONObject(body);
            if (callback != null) {
                callback.call(response);
            }
            // Close streams and disconnect.
            connection.disconnect();
            os.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}