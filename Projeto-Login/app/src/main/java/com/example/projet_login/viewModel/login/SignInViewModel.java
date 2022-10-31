package com.example.projet_login.viewModel.login;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projet_login.repository.firebase.FirebaseRepository;
import com.example.projet_login.repository.google.GoogleSignInRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

public class SignInViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> loggedWithSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBarStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpButtonClick = new MutableLiveData<>();
    private final FirebaseRepository firebaseRepository;
    private final GoogleSignInRepository googleSignInRepository;


    public SignInViewModel(Application application) {
        super(application);
        progressBarStatus.setValue(false);
        loggedWithSuccess.setValue(false);
        firebaseRepository = new FirebaseRepository(application);
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

    public void signUp(String email, String password) {
        if ((!email.isEmpty() && !password.isEmpty()))
            firebaseRepository.createUserWithEmail(email, password);
    }

    public Boolean isUserLogged() {
        return firebaseRepository.isUserLogged();
    }
}
