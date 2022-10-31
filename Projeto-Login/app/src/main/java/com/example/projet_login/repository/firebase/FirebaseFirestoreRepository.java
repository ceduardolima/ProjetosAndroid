package com.example.projet_login.repository.firebase;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.projet_login.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.Consumer;

public class FirebaseFirestoreRepository {
    private final FirebaseFirestore firestore;
    private final Application application;

    public FirebaseFirestoreRepository(Application application) {
        firestore = FirebaseFirestore.getInstance();
        this.application = application;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void registerUser(User user, Consumer<Boolean> block) {
        firestore.collection("users")
                .add(user)
                .addOnSuccessListener((documentReference -> {
                    block.accept(true);
                }));
    }
}
