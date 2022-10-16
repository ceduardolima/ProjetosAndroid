package com.example.projet_login.repository.google;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

public class GoogleSignInRepository {
    private final GoogleSignInOptions googleSignInOptions;
    private final GoogleSignInClient googleSignInClient;
    private final GoogleSignInAccount account;

    public GoogleSignInRepository(Context context) {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
        account = GoogleSignIn.getLastSignedInAccount(context);
        googleSignInClient.signOut();
    }

    public Intent getSignInIntent() {
        return this.googleSignInClient.getSignInIntent();
    }

    public Task<GoogleSignInAccount> getSignedInAccountFromIntent(Intent data) {
        return GoogleSignIn.getSignedInAccountFromIntent(data);
    }

    public Boolean hasSignInBefore() {
        return account != null;
    }

    public GoogleSignInAccount getAccount() {
        return account;
    }
}
