package com.biz4solutions.utilities;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Created by amrut.bidri on 11-05-2017.
 */

public class GetDirectionsParseDirectionsTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    private final GetDirectionsCallback getDirectionsCallback;
    private String distance = "";
    private String duration = "";

    public GetDirectionsParseDirectionsTask(GetDirectionsCallback getDirectionsCallback) {
        this.getDirectionsCallback = getDirectionsCallback;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);

            // Starts parsing data
            routes = parseDirections(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (getDirectionsCallback != null) {
            getDirectionsCallback.showProgress();
        }
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> results) {
        super.onPostExecute(results);
        if (getDirectionsCallback != null) {
            if (results != null) {
                getDirectionsCallback.onSuccess(results, distance, duration);
            } else {
                getDirectionsCallback.onFailure("");
            }
            getDirectionsCallback.hideProgress();
        }
    }

    private List<List<HashMap<String, String>>> parseDirections(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /* Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /* Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    distance = ((JSONObject) jLegs.get(j)).getJSONObject("distance").getString("text");
                    duration = ((JSONObject) jLegs.get(j)).getJSONObject("duration").getString("text");
                    /* Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /* Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

            if (distance != null && !distance.isEmpty()) {
                String[] dist = distance.split(" ");
                if (dist.length > 1) {
                    if (dist[1].toLowerCase().contains("km")) {
                        DecimalFormat df = new DecimalFormat("#.##");
                        String temp = df.format((Double.parseDouble(dist[0]) * 0.621371d));
                        distance = Double.parseDouble(temp) + " Miles";
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
