package com.example.projet_login.repository.firebase;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.projet_login.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.function.Consumer;

public class FirebaseAuthRepository {
    private final FirebaseAuth auth;
    private final Application application;
    private FirebaseUser currentUser;

    public FirebaseAuthRepository(Application application) {
        auth = FirebaseAuth.getInstance();
        this.application = application;
        currentUser = auth.getCurrentUser();
    }

    public Boolean isUserLogged() {
        return (currentUser != null);
    }

    public FirebaseUser getUser() {
        return currentUser;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createUserWithEmail(User user, String password, Consumer<Boolean> success) {
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener((OnCompleteListener<AuthResult>) task -> {
                  if (task.isSuccessful()) {
                      currentUser = auth.getCurrentUser();
                      success.accept(true);
                      printMessage("Account created with success!");
                  }
                  else {
                      handleAuthenticationExceptions(task);
                      success.accept(false);
                  }
                });
    }

    private void handleAuthenticationExceptions(Task task) {
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseAuthWeakPasswordException e) {
            printMessage("Password must have, at least, 6 character.");
        } catch (FirebaseAuthInvalidCredentialsException e) {
            printMessage("Invalid credential");
        } catch(FirebaseAuthInvalidUserException e) {
            printMessage("Account doesn't exist.");
        } catch (Exception e) {
            printMessage(e.getMessage());
        }
    }

    private void printMessage(String message) {
        Toast.makeText(application, message, Toast.LENGTH_SHORT).show();
    }

}
