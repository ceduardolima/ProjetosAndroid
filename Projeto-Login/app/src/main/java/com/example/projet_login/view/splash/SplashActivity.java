package com.example.projet_login.view.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.projet_login.R;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initializeView();
    }

    private void initializeView() {
        progressBar = findViewById(R.id.splash_progress_bar);
        icon = findViewById(R.id.splash_icon);
    }
}