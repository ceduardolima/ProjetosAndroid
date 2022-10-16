package com.example.projet_login.view.login.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projet_login.R;
import com.example.projet_login.databinding.FragmentSignInBinding;
import com.example.projet_login.view.profile.ProfileActivity;
import com.example.projet_login.viewModel.login.SignInViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private SignInViewModel signInViewModel;
    private final int RC_SIGN_IN = 0;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleSignIn();
        initViewModel();
        observerProgressBarStatus();
    }

    private void initGoogleSignIn() {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions);
    }

    private void initViewModel() {
        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
    }

    private void observerProgressBarStatus() {
        signInViewModel.getProgressBarStatus().observe(requireActivity(), (status) -> {
            if (status)
                ableProgressBar();
            else
                disableProgressBar();
        });
    }

    public void ableProgressBar() {
        binding.signInContainerView.setVisibility(View.INVISIBLE);
        binding.signInProgressBar.setVisibility(View.VISIBLE);
    }

    public void disableProgressBar() {
        binding.signInContainerView.setVisibility(View.VISIBLE);
        binding.signInProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        signInButton();
        return binding.getRoot();
    }

    private void signInButton() {
        binding.signInGoogle.setOnClickListener((view) -> signIn());
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            signInViewModel.setProgressBarStatus(true);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            signInViewModel.setProgressBarStatus(false);
            navigateToProfileActivity();
        } catch (ApiException e) {
            Log.w("google_sign_in", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void navigateToProfileActivity() {
        Intent intent = new Intent(requireContext(), ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}