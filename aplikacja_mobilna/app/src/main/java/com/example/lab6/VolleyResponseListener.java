package com.example.lab6;

import org.json.JSONArray;

public interface VolleyResponseListener {
    void onError(String message);
    void onResponse(JSONArray response);
}
