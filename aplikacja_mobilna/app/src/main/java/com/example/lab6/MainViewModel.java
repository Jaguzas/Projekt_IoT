package com.example.lab6;

import com.android.volley.RequestQueue;
import com.example.lab6.MeasurementModel;
import com.example.lab6.ServerIoT;
import com.example.lab6.VolleyResponseListener;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;
import java.util.Timer;
import java.util.TimerTask;

public class MainViewModel extends ViewModel {

    private MeasurementsAdapter adapter;
    private List<MeasurementViewModel> measurements;
    private ServerIoT server;
    private int sampleTime = Common.DEFAULT_SAMPLE_TIME;

    /* BEGIN request timer */
    private RequestQueue queue;
    private Timer requestTimer;
    private long requestTimerTimeStamp = 0;
    private long requestTimerPreviousTime = -1;
    private boolean requestTimerFirstRequest = true;
    private boolean requestTimerFirstRequestAfterStop;
    private TimerTask requestTimerTask;
    private final Handler handler = new Handler();
    /* END request timer */

    /**
     * @brief MainViewModel initialization: creation of measurement list view adapter, measurements
     *        list container and IoT server API configuration.
     * @param context Activity context
     */
    public void Init(Context context, String url) {
        measurements = new ArrayList<>();
        adapter = new MeasurementsAdapter(measurements);

        server = new ServerIoT(context.getApplicationContext(),
                new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Log.d("Response error", message);
                    }
                    @Override
                    public void onResponse(JSONArray response) {
                        int rs = response.length();
                        int ms = measurements.size();
                        int sizeDiff = ms - rs;
                        // remove redundant measurements from list
                        for( int i = 0; i < sizeDiff; i++) {
                            measurements.remove(ms - 1 - i);
                            adapter.notifyItemRemoved(ms - 1 - i);
                        }
                        // iterate through JSON Array
                        for (int i = 0; i < rs; i++) {
                            try {
                                /* get measurement model from JSON data */
                                MeasurementModel measurement = new MeasurementModel(response.getJSONObject(i));

                                /* update measurements list */
                                if(i >= ms) {
                                    measurements.add(measurement.toVM());
                                    adapter.notifyItemInserted(i);
                                } else {
                                    measurements.set(i, measurement.toVM());
                                    adapter.notifyItemChanged(i);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        server.setUrl( url /*"http://192.168.56.15/czujniki.php"*/);
        startRequestTimer();
    }


    /**
     * @brief Getter of 'adapter' field.
     * @return Measurement list view adapter.
     */
    public MeasurementsAdapter getAdapter() {
        return adapter;
    }

    /**
     * @brief 'Refresh' button onClick event handler.
     * @param v 'Refresh' button view
     */
    public void updateMeasurements(View v) {

        // server.getMeasurements();

    }

    /**
     * @brief URL getter method.
     * @return Server resource URL
     */
    public String getUrl() {
        return server.getUrl();
    }

    /**
     * @brief URL setter method.
     * @param url Server resource URL
     */
    public void setUrl(String url) {
        server.setUrl(url);
    }

    /**
     * @brief Starts new 'Timer' (if currently not exist) and schedules periodic task.
     */
    private void startRequestTimer() {
        if(requestTimer == null) {
            // set a new Timer
            requestTimer = new Timer();

            // initialize the TimerTask's job
            initializeRequestTimerTask();
            requestTimer.schedule(requestTimerTask, 0, sampleTime);


        }
    }

    /**
     * @brief Stops request timer (if currently exist)
     * and sets 'requestTimerFirstRequestAfterStop' flag.
     */
    private void stopRequestTimerTask() {
        // stop the timer, if it's not already null
        if (requestTimer != null) {
            requestTimer.cancel();
            requestTimer = null;
            requestTimerFirstRequestAfterStop = true;
        }
    }

    /**
     * @brief Initialize request timer period task with 'Handler' post method as 'sendGetRequest'.
     */
    private void initializeRequestTimerTask() {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() { server.getMeasurements(); }
                });
            }
        };
    }


}