package com.example.projet_login.view.login.fragment;

import static android.app.Activity.RESULT_OK;

import android.accounts.Account;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.projet_login.R;
import com.example.projet_login.databinding.FragmentLoginBinding;
import com.example.projet_login.view.profile.ProfileActivity;
import com.example.projet_login.viewModel.login.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private Dialog signUpDialog;
    private Dialog signInDialog;
    private CircleImageView signUpUserImageButton;
    private final int RC_SIGN_IN = 0;
    private final int RC_GALLERY = 1;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        observerProgressBarStatus();
        observerLoggedWithSuccess();
        observerSignUpButton();
    }

    private void initViewModel() {
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    private void observerProgressBarStatus() {
        loginViewModel.getProgressBarStatus().observe(requireActivity(), (status) -> {
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
        loginViewModel.getLoggedWithSuccess().observe(requireActivity(), (success) -> {
            if (success) changeToProfileActivity();
        });
    }

    private void changeToProfileActivity() {
        Intent intent = new Intent(requireContext(), ProfileActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void observerSignUpButton() {
        loginViewModel.getSignUpButtonClick().observe(requireActivity(), click -> {
            if (click) {
                Window dialogWindow = signUpDialog.getWindow();
                createAccount(dialogWindow);
            }
            loginViewModel.setSignUpButtonClick(false);
        });
    }

    private void createAccount(Window dialogWindow) {
        TextInputEditText emailInput = dialogWindow.findViewById(R.id.sign_up_email_input);
        TextInputEditText passwordInput = dialogWindow.findViewById(R.id.sign_up_password_input);
        TextInputEditText usernameInput = dialogWindow.findViewById(R.id.sign_up_email_input_layout);
        String email = Objects.requireNonNull(emailInput.getText()).toString();
        String password = Objects.requireNonNull(passwordInput.getText()).toString();
        String username = Objects.requireNonNull(usernameInput.getText()).toString();
        loginViewModel.signUp(email, password, username);
    }

    @Override
    public void onStart() {
        super.onStart();
        //verifyIfHasSignInBefore();
    }

    private void verifyIfHasSignInBefore() {
        if (loginViewModel.isUserLogged())
            changeToProfileActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        signInGoogleButton();
        signInEmailButton();
        signUpButton();
        return binding.getRoot();
    }

    private void signInGoogleButton() {
        binding.signInGoogleButton.setOnClickListener((view) -> signIn());
    }

    private void signIn() {
        Intent signInIntent = loginViewModel.getGoogleSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_SIGN_IN:
                    Task<GoogleSignInAccount> task = loginViewModel.getGoogleSignInAccountFromIntent(data);
                    handleSignInResult(task);

                case RC_GALLERY:
                    setImageOnSignUpUserImageButton(data.getData());
            }
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

    private void setImageOnSignUpUserImageButton(Uri uri) {
        if (uri != null) {
            signUpUserImageButton.setImageURI(uri);
            signUpUserImageButton.setPadding(2, 2, 2, 2);
        }
    }

    private void setLoggedWithSuccessIfAccountIsNotNull(Account account) {
        if (account != null) {
            loginViewModel.setProgressBarStatus(false);
            loginViewModel.setLoggedWithSuccess(true);
        }
    }

    private void signInEmailButton() {
        binding.signInEmailButton.setOnClickListener(view -> createSignInDialog());
    }

    private void createSignInDialog() {
        signInDialog = new Dialog(requireActivity(), R.style.DialogSlideAnim);
        configSignInDialog(signInDialog);
        signInDialog.show();
    }

    private void configSignInDialog(Dialog dialog) {
        dialog.setContentView(R.layout.dialog_sign_in_email);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialogSignInButton(dialog.getWindow());
        dialogCloseButton(dialog, R.id.sign_in_close_dialog_button);
        dialog.create();
    }

    private void dialogSignInButton(Window dialogWindow) {
        Button signInButton = dialogWindow.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> {
            // Login
        });
    }

    private void dialogCloseButton(Dialog dialog, int buttonID) {
        ImageButton closeDialogButton =
                dialog.getWindow().findViewById(buttonID);
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
        dialogSignUpUserImageButton(dialog.getWindow());
        dialogCloseButton(dialog, R.id.sign_up_close_dialog_button);
        dialog.create();
    }

    private void dialogSignUpButton(Window dialogWindow) {
        Button signUpButton = dialogWindow.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(view -> loginViewModel.setSignUpButtonClick(true));
    }

    private void dialogSignUpUserImageButton(Window dialogWindow) {
        signUpUserImageButton = dialogWindow.findViewById(R.id.sign_up_user_image_button);
        signUpUserImageButton.setOnClickListener(view -> startGalleryActivity());
    }

    private void startGalleryActivity() {
        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(iGallery, RC_GALLERY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}