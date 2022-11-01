package com.example.projet_login.viewModel.login;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projet_login.model.User;
import com.example.projet_login.repository.firebase.FirebaseAuthRepository;
import com.example.projet_login.repository.firebase.FirebaseFirestoreRepository;
import com.example.projet_login.repository.google.GoogleSignInRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> loggedWithSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> accountCreatedWithSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBarStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpButtonClick = new MutableLiveData<>();
    private final FirebaseAuthRepository firebaseAuthRepository;
    private final GoogleSignInRepository googleSignInRepository;
    private final FirebaseFirestoreRepository firestoreRepository;

    public LiveData<Boolean> getAccountCreatedWithSuccess() {
        return accountCreatedWithSuccess;
    }

    public LoginViewModel(Application application) {
        super(application);
        progressBarStatus.setValue(false);
        loggedWithSuccess.setValue(false);
        firebaseAuthRepository = new FirebaseAuthRepository(application);
        firestoreRepository = new FirebaseFirestoreRepository(application);
        googleSignInRepository = new GoogleSignInRepository(application);
    }

    public void setLoggedWithSuccess(Boolean loggedWithSuccess) {
        this.loggedWithSuccess.postValue(loggedWithSuccess);
    }

    public void setProgressBarStatus(Boolean progressBarStatus) {
        this.progressBarStatus.postValue(progressBarStatus);
    }

    public LiveData<Boolean> getLoggedWithSuccess() {
        return loggedWithSuccess;
    }

    public LiveData<Boolean> getProgressBarStatus() {
        return progressBarStatus;
    }

    public LiveData<Boolean> getSignUpButtonClick() {
        return signUpButtonClick;
    }

    public void setSignUpButtonClick(Boolean click) {
        this.signUpButtonClick.postValue(click);
    }

    public Intent getGoogleSignInIntent() {
       return googleSignInRepository.getSignInIntent();
    }

    public Task<GoogleSignInAccount> getGoogleSignInAccountFromIntent(Intent data) {
        return googleSignInRepository.getSignedInAccountFromIntent(data);
    }

    public void signUp(String email, String password, String username) {
        if ((!email.isEmpty() && !password.isEmpty() && !username.isEmpty())) {
            User user = createUser(email, username, "");
            authenticateAndRegisterUser(user, password);
        }
    }

    public User createUser(String email, String username, String photoPath) {
        return new User(username, email, photoPath);
    }

    private void authenticateAndRegisterUser(User user, String password) {
        firebaseAuthRepository.createUserWithEmail(
                user,
                password,
                (success) -> {
                    if (success) {
                        registerUserOnFirestore(user);
                    }
                }
        );
    }

    public void registerUserOnFirestore(User user) {
        String uId = firebaseAuthRepository.getUser().getUid();
        user.setId(uId);
        firestoreRepository.registerUser(user, accountCreatedWithSuccess::postValue);
    }

    public Boolean isUserLogged() {
        return firebaseAuthRepository.isUserLogged();
    }
}
