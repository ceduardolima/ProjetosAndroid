package com.example.projet_login.viewModel.login;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.example.projet_login.repository.firebase.FirebaseRepository;
import com.example.projet_login.view.login.LoginActivity;

public class SignInViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> loggedWithSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBarStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpButtonClick = new MutableLiveData<>();
    private final FirebaseRepository firebaseRepository;


    public SignInViewModel(Application application) {
        super(application);
        progressBarStatus.setValue(false);
        loggedWithSuccess.setValue(false);
        firebaseRepository = new FirebaseRepository(application);
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

    public void signUp(String email, String password) {
        if ((!email.isEmpty() && !password.isEmpty()))
            firebaseRepository.createUserWithEmail(email, password);
    }
}
