package com.example.projet_login.viewModel.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignInViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loggedWithSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBarStatus = new MutableLiveData<>();

    public SignInViewModel() {
        progressBarStatus.setValue(false);
        loggedWithSuccess.setValue(false);
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
}
