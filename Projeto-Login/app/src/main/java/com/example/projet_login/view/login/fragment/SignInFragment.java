package com.example.projet_login.view.login.fragment;

import android.accounts.Account;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.projet_login.R;
import com.example.projet_login.databinding.FragmentSignInBinding;
import com.example.projet_login.repository.google.GoogleSignInRepository;
import com.example.projet_login.view.profile.ProfileActivity;
import com.example.projet_login.viewModel.login.SignInViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SignInFragment extends Fragment {
    private GoogleSignInRepository googleSignInRepository;
    private FragmentSignInBinding binding;
    private SignInViewModel signInViewModel;
    private Dialog signUpDialog;
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
        observerSignUpButton();
    }

    private void initViewModel() {
        signInViewModel = new ViewModelProvider(requireActivity()).get(SignInViewModel.class);
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

    private void observerSignUpButton() {
        signInViewModel.getSignUpButtonClick().observe(requireActivity(), click -> {
            if (click) {
                Window dialogWindow = signUpDialog.getWindow();
                createAccount(dialogWindow);
            }
            signInViewModel.setSignUpButtonClick(false);
        });
    }

    private void createAccount(Window dialogWindow) {
        TextInputEditText emailInput = dialogWindow.findViewById(R.id.sign_up_email_input);
        TextInputEditText passwordInput = dialogWindow.findViewById(R.id.sign_up_password_input);
        String email = Objects.requireNonNull(emailInput.getText()).toString();
        String password = Objects.requireNonNull(passwordInput.getText()).toString();
        signInViewModel.signUp(email, password);
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
        signInGoogleButton();
        signInEmailButton();
        signUpButton();
        return binding.getRoot();
    }

    private void signInGoogleButton() {
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
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            setLoggedWithSuccessIfAccountIsNotNull(account.getAccount());
        } catch (ApiException e) {
            Log.w("google_sign_in", "signInResult:failed code=" + e.getStatusCode());
            setLoggedWithSuccessIfAccountIsNotNull(null);
        }
    }

    private void setLoggedWithSuccessIfAccountIsNotNull(Account account) {
        if (account != null) {
            signInViewModel.setProgressBarStatus(false);
            signInViewModel.setLoggedWithSuccess(true);
        }
    }

    private void signInEmailButton() {
        binding.signInEmailButton.setOnClickListener(view -> createSignInDialog());
    }

    private void createSignInDialog() {
        final Dialog dialog = new Dialog(requireActivity(), R.style.DialogSlideAnim);
        configDialog(dialog);
        dialog.show();
    }

    private void configDialog(Dialog dialog) {
        dialog.setContentView(R.layout.dialog_sign_in_email);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialogSignInButton(dialog.getWindow());
        dialogCloseButton(dialog);
        dialog.create();
    }

    private void dialogSignInButton(Window dialogWindow) {
        Button signInButton = dialogWindow.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> {
            // Login
        });
    }

    private void dialogCloseButton(Dialog dialog) {
        ImageButton closeDialogButton =
                dialog.getWindow().findViewById(R.id.sign_in_close_dialog_button);
        closeDialogButton.setOnClickListener(view -> dialog.cancel());

    }

    private void signUpButton() {
        binding.signInCreateAccountButton.setOnClickListener(view -> createSignUpDialog());
    }

    private void createSignUpDialog() {
        signUpDialog = new Dialog(requireActivity(), R.style.DialogSlideAnim);
        configSignUpDialog(signUpDialog);
        signUpDialog.show();
    }

    private void configSignUpDialog(Dialog dialog) {
        dialog.setContentView(R.layout.dialog_sign_up);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialogSignUpButton(dialog.getWindow());
        dialogSignUpCloseButton(dialog);
        dialog.create();
    }

    private void dialogSignUpButton(Window dialogWindow) {
        Button signUpButton = dialogWindow.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(view -> {
            signInViewModel.setSignUpButtonClick(true);
        });
    }

    private void dialogSignUpCloseButton(Dialog dialog) {
        ImageButton closeDialogButton = dialog.getWindow().findViewById(R.id.sign_up_close_dialog_button);
        closeDialogButton.setOnClickListener(view -> dialog.cancel());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}