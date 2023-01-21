package com.example.lab6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

// VIEW MODELS
import com.example.lab6.MainViewModel;
//import com.example.lab6.MainViewModelMock;

// Automatically generated class

import com.example.lab6.R;
import com.example.lab6.databinding.ActivityCzujnikiBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

public class Czujniki extends AppCompatActivity {

    private ActivityCzujnikiBinding binding;

    private MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_czujniki);

        String ipAddress = "";
        Intent intent = getIntent();
        Bundle configBundle = intent.getExtras();
        if(configBundle != null) {
            ipAddress = configBundle.getString(Common.CONFIG_IP_ADDRESS, Common.DEFAULT_IP_ADDRESS);
        }
        String urlText = "http://" + ipAddress + "/czujniki.php";

        // Create new view model provider with MainViewModel class
        //viewModel = new ViewModelProvider(this).get(MainViewModelMock.class);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if (savedInstanceState == null)
            viewModel.Init(this, urlText); // Initialize only if activity instance state is empty

        // Data binding utility
        binding = DataBindingUtil.setContentView(this, R.layout.activity_czujniki);
        // Binding data context of activity_main: setting view_model variable
        binding.setViewModel(viewModel);
    }

}