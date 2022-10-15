package com.example.projet_login.view.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBindings;

import android.os.Bundle;
import android.view.View;

import com.example.projet_login.R;
import com.example.projet_login.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

    }
}