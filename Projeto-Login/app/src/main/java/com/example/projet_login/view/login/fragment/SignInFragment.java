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
import com.example.projet_login.repository.google.GoogleSignInRepository;
import com.example.projet_login.view.login.LoginActivity;
import com.example.projet_login.view.profile.ProfileActivity;
import com.example.projet_login.viewModel.login.SignInViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class SignInFragment extends Fragment {
    private GoogleSignInRepository googleSignInRepository;
    private FragmentSignInBinding binding;
    private SignInViewModel signInViewModel;
    private final int RC_SIGN_IN = 0;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleSignInRepository = new GoogleSignInRepository(requireContext());
        initViewModel();
        observerProgressBarStatus();
        observerLoggedWithSuccess();
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

    private void ableProgressBar() {
        binding.signInContainerView.setVisibility(View.INVISIBLE);
        binding.signInProgressBar.setVisibility(View.VISIBLE);
    }

    private void disableProgressBar() {
        binding.signInContainerView.setVisibility(View.VISIBLE);
        binding.signInProgressBar.setVisibility(View.INVISIBLE);
    }

    private void observerLoggedWithSuccess() {
        signInViewModel.getLoggedWithSuccess().observe(requireActivity(), (success) -> {
            if (success) changeToProfileActivity();
        });
    }

    private void changeToProfileActivity() {
        Intent intent = new Intent(requireContext(), ProfileActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        verifyIfHasSignInBefore();
    }

    private void verifyIfHasSignInBefore() {
        if (googleSignInRepository.hasSignInBefore())
            changeToProfileActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        signInButton();
        return binding.getRoot();
    }

    private void signInButton() {
        binding.signInGoogleButton.setOnClickListener((view) -> signIn());
    }

    private void signIn() {
        Intent signInIntent = googleSignInRepository.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = googleSignInRepository.getSignedInAccountFromIntent(data);
            signInViewModel.setProgressBarStatus(true);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            signInViewModel.setProgressBarStatus(false);
            signInViewModel.setLoggedWithSuccess(true);
        } catch (ApiException e) {
            Log.w("google_sign_in", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}