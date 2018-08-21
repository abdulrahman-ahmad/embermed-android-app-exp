package com.biz4solutions.provider.utilities;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * Created by ketan on 11-05-2017.
 */

public class GetDirectionsTask extends AsyncTask<String, Void, String> {

    private final GetDirectionsCallback getDirectionsCallback;

    public GetDirectionsTask(GetDirectionsCallback getDirectionsCallback) {
        this.getDirectionsCallback = getDirectionsCallback;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("Exception while downloading url", e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (getDirectionsCallback != null) {
            getDirectionsCallback.showProgress();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(params[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (getDirectionsCallback != null) {
            getDirectionsCallback.hideProgress();
        }
        GetDirectionsParseDirectionsTask parserTask = new GetDirectionsParseDirectionsTask(getDirectionsCallback);

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);
    }
}
