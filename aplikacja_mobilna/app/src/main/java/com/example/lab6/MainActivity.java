package com.example.lab6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private String ipAddress = Common.DEFAULT_IP_ADDRESS;
    private int sampleTime = Common.DEFAULT_SAMPLE_TIME;

    private Button button_wykresy;
    private Button button_LED;
    private Button button_dane;
    private Button button_konfiguracja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_wykresy = (Button) findViewById(R.id.button_wykresy);
        button_LED = (Button) findViewById(R.id.button_LED);
        button_dane = (Button) findViewById(R.id.button_dane);
        button_konfiguracja = (Button) findViewById(R.id.configBtn);

        button_wykresy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWykresy();
            }
        });
        button_LED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLED();
            }
        });
        button_dane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDane();
            }
        });
        button_konfiguracja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfig();
            }
        });
    }

    public void openWykresy() {
        Intent intent = new Intent(this, Wykresy.class);
        Bundle configBundle = new Bundle();
        configBundle.putString(Common.CONFIG_IP_ADDRESS, ipAddress);
        configBundle.putInt(Common.CONFIG_SAMPLE_TIME, sampleTime);
        intent.putExtras(configBundle);
        startActivity(intent);
    }

    public void openLED() {
        Intent intent = new Intent(this, LED.class);
        Bundle configBundle = new Bundle();
        configBundle.putString(Common.CONFIG_IP_ADDRESS, ipAddress);
        intent.putExtras(configBundle);
        startActivity(intent);
    }

    public void openDane() {
        Intent intent = new Intent(this, Czujniki.class);
        Bundle configBundle = new Bundle();
        configBundle.putString(Common.CONFIG_IP_ADDRESS, ipAddress);
        intent.putExtras(configBundle);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if ((requestCode == Common.REQUEST_CODE_CONFIG) && (resultCode == RESULT_OK)) {

            // IoT server IP address
            ipAddress = dataIntent.getStringExtra(Common.CONFIG_IP_ADDRESS);
            //textViewIP.setText(getIpAddressDisplayText(ipAddress));

            // Sample time (ms)
            String sampleTimeText = dataIntent.getStringExtra(Common.CONFIG_SAMPLE_TIME);
            sampleTime = Integer.parseInt(sampleTimeText);
            //textViewSampleTime.setText(getSampleTimeDisplayText(sampleTimeText));
        }
    }


    public void openConfig() {
        Intent intent = new Intent(this, ConfigActivity.class);
        Bundle configBundle = new Bundle();
        configBundle.putString(Common.CONFIG_IP_ADDRESS, ipAddress);
        configBundle.putInt(Common.CONFIG_SAMPLE_TIME, sampleTime);
        intent.putExtras(configBundle);
        startActivityForResult(intent, Common.REQUEST_CODE_CONFIG);
    }
}