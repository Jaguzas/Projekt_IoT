package com.example.lab6;

import org.json.JSONException;
import org.json.JSONObject;

public class HandlerDanych {
    /**
     * @brief Reading raw chart data from JSON response.
     * @param response IoT server JSON response as string
     * @retval new chart data
     */
    public double getRawDataFromResponse(String response) {
        JSONObject jObject;
        double x = Double.NaN;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x;
        }

        // Read chart data form JSON object
        try {
            x = jObject.getDouble("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }
}
