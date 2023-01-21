package com.example.lab6;

import static java.lang.Double.isNaN;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;


public class Wykresy extends AppCompatActivity {


    private String ipAddress = Common.DEFAULT_IP_ADDRESS;
    private int sampleTime = Common.DEFAULT_SAMPLE_TIME;
    /* END config data */

    /* BEGIN widgets */
    private TextView textViewIP;
    private TextView textViewSampleTime;
    private TextView textViewError;
    private GraphView dataGraph;
    private LineGraphSeries[] dataSeries;
    private final int dataGraphMaxDataPointsNumber = 1000;
    private final double dataGraphMaxX = 10.0d;
    private final double dataGraphMinX =  0.0d;
    private final double dataGraphMaxY =  360;
    private final double dataGraphMinY =  0;
    private AlertDialog.Builder configAlterDialog;
    /* END widgets */

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

    /* Testable module */
    private HandlerDanych responseHandling = new HandlerDanych();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wykresy);

        // get the Intent that started this Activity
        Intent intent = getIntent();

        // get the Bundle that stores the data of this Activity
        Bundle configBundle = intent.getExtras();

        if(configBundle != null) {
            ipAddress = configBundle.getString(Common.CONFIG_IP_ADDRESS, Common.DEFAULT_IP_ADDRESS);
            sampleTime = configBundle.getInt(Common.CONFIG_SAMPLE_TIME, Common.DEFAULT_SAMPLE_TIME);
        }


        /* BEGIN initialize widgets */
        /* BEGIN initialize TextViews */
        textViewIP = findViewById(R.id.textViewIP);
        textViewIP.setText(getIpAddressDisplayText(ipAddress));

        textViewSampleTime = findViewById(R.id.textViewSampleTime);
        textViewSampleTime.setText(getSampleTimeDisplayText(Integer.toString(sampleTime)));

        textViewError = findViewById(R.id.textViewErrorMsg);
        textViewError.setText("");
        /* END initialize TextViews */

        /* BEGIN initialize GraphView */
        // https://github.com/jjoe64/GraphView/wiki
        dataGraph = (GraphView)findViewById(R.id.dataGraph);
        //dataSeries = new LineGraphSeries[](new DataPoint[]{}, new DataPoint[]{}, new DataPoint[]{});
        dataSeries = new LineGraphSeries[]{ new LineGraphSeries<>(new DataPoint[]{}),
                new LineGraphSeries<>(new DataPoint[]{}),new LineGraphSeries<>(new DataPoint[]{})};

        dataGraph.addSeries(dataSeries[0]);
        dataGraph.addSeries(dataSeries[1]);
        dataGraph.addSeries(dataSeries[2]);
        dataGraph.getViewport().setXAxisBoundsManual(true);
        dataGraph.getViewport().setMinX(dataGraphMinX);
        dataGraph.getViewport().setMaxX(dataGraphMaxX);
        dataGraph.getViewport().setYAxisBoundsManual(true);
        dataGraph.getViewport().setMinY(dataGraphMinY);
        dataGraph.getViewport().setMaxY(dataGraphMaxY);

        dataGraph.getLegendRenderer().setVisible(true);
        dataGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        dataGraph.getLegendRenderer().setTextSize(30);

        dataGraph.getGridLabelRenderer().setTextSize(20);
        dataGraph.getGridLabelRenderer().setVerticalAxisTitle("Kąt [deg]");
        dataGraph.getGridLabelRenderer().setHorizontalAxisTitle("Czas [s]");
        dataGraph.getGridLabelRenderer().setNumHorizontalLabels(9);
        dataGraph.getGridLabelRenderer().setNumVerticalLabels(7);
        dataGraph.getGridLabelRenderer().setPadding(35);


        dataSeries[0].setTitle("Roll");
        dataSeries[0].setColor(Color.BLUE);
        dataSeries[1].setTitle("Pitch");
        dataSeries[1].setColor(Color.GREEN);
        dataSeries[2].setTitle("Yaw");
        dataSeries[2].setColor(Color.RED);

        /* END initialize GraphView */


        // Initialize Volley request queue
        queue = Volley.newRequestQueue(Wykresy.this);


    }


    /**
     * @brief Main activity button onClick procedure - common for all upper menu buttons
     * @param v the View (Button) that was clicked
     */
    public void btns_onClick(View v) {
        switch (v.getId()) {

            case R.id.startBtn: {
                startRequestTimer();
                break;
            }
            case R.id.stopBtn: {
                stopRequestTimerTask();
                break;
            }
            default: {
                // do nothing
            }
        }
    }

    /**
     * @brief Create display text for IoT server IP address
     * @param ip IP address (string)
     * @retval Display text for textViewIP widget
     */
    private String getIpAddressDisplayText(String ip) {
        return ("IP: " + ip);
    }

    /**
     * @brief Create display text for requests sample time
     * @param st Sample time in ms (string)
     * @retval Display text for textViewSampleTime widget
     */
    private String getSampleTimeDisplayText(String st) {
        return ("Czas próbkowania: " + st + " ms");
    }

    /**
     * @brief Create JSON file URL from IoT server IP.
     * @param ip IP address (string)
     * @retval GET request URL
     */
    private String getURL(String ip) {
        return ("http://" + ip + "/" + Common.FILE_NAME);
    }

    /**
     * @brief Handles application errors. Logs an error and passes error code to GUI.
     * @param errorCode local error codes, see: COMMON
     */
    private void errorHandling(int errorCode) {
        switch(errorCode) {
            case Common.ERROR_TIME_STAMP:
                textViewError.setText("ERR #1");
                Log.d("errorHandling", "Request time stamp error.");
                break;
            case Common.ERROR_NAN_DATA:
                textViewError.setText("ERR #2");
                Log.d("errorHandling", "Invalid JSON data.");
                break;
            case Common.ERROR_RESPONSE:
                textViewError.setText("ERR #3");
                Log.d("errorHandling", "GET request VolleyError.");
                break;
            default:
                textViewError.setText("ERR ??");
                Log.d("errorHandling", "Unknown error.");
                break;
        }
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

            // clear error message
            textViewError.setText("");
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
                    public void run() { sendGetRequest(); }
                });
            }
        };
    }

    /**
     * @brief Sending GET request to IoT server using 'Volley'.
     */
    private void sendGetRequest()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURL(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandling(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorHandling(Common.ERROR_RESPONSE);
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * @brief Validation of client-side time stamp based on 'SystemClock'.
     */
    private long getValidTimeStampIncrease(long currentTime)
    {
        // Right after start remember current time and return 0
        if(requestTimerFirstRequest)
        {
            requestTimerPreviousTime = currentTime;
            requestTimerFirstRequest = false;
            return 0;
        }

        // After each stop return value not greater than sample time
        // to avoid "holes" in the plot
        if(requestTimerFirstRequestAfterStop)
        {
            if((currentTime - requestTimerPreviousTime) > sampleTime)
                requestTimerPreviousTime = currentTime - sampleTime;

            requestTimerFirstRequestAfterStop = false;
        }

        // If time difference is equal zero after start
        // return sample time
        if((currentTime - requestTimerPreviousTime) == 0)
            return sampleTime;

        // Return time difference between current and previous request
        return (currentTime - requestTimerPreviousTime);
    }

    public double searchKey(JSONArray jsonArray, String key) {
        double value = 0;
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject item = null;
            try {
                item = jsonArray.getJSONObject(i);
                value = item.getDouble(key);
                break;
            } catch (JSONException e) {
            }
        }
        return value;
    }

    public double searchNamedValue(JSONArray array, String searchValue){
        double value = 0;
        JSONArray filtedArray = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj= null;
            try {
                obj = array.getJSONObject(i);
                if(obj.getString("name").equals(searchValue))
                {
                    // filtedArray.put(obj);
                    value = obj.getDouble("value");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // String result = filtedArray.toString();
        return value;
    }

    /**
     * @brief GET response handling - chart data series updated with IoT server data.
     */
    private void responseHandling(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            double roll = 0, pitch =0, yaw = 0;
            // get raw data from JSON response
            try {
                JSONArray data = new JSONArray(response);
                // roll = searchKey(data,"roll");
                // pitch = searchKey(data,"pitch");
                // yaw = searchKey(data,"yaw");
                roll = searchNamedValue(data, "roll");
                pitch = searchNamedValue(data, "pitch");
                yaw = searchNamedValue(data, "yaw");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // update chart

            if (isNaN(roll)) {
                errorHandling(Common.ERROR_NAN_DATA);

            } else {
                // update plot series
                double timeStamp = requestTimerTimeStamp / 1000.0; // [sec]
                boolean scrollGraph = (timeStamp > dataGraphMaxX);
                dataSeries[0].appendData(new DataPoint(timeStamp, roll), scrollGraph, dataGraphMaxDataPointsNumber);
                dataSeries[1].appendData(new DataPoint(timeStamp, pitch), scrollGraph, dataGraphMaxDataPointsNumber);
                dataSeries[2].appendData(new DataPoint(timeStamp, yaw), scrollGraph, dataGraphMaxDataPointsNumber);

                // refresh chart
                dataGraph.onDataChanged(true, true);
            }


            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
}