package com.example.myapplication.controller;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.model.ColorRule;
import com.example.myapplication.model.User;

import java.util.concurrent.CompletableFuture;


public class UserController extends FireBaseController {
    public UserController() {
        super();
    }

    public UserController(Context context) {
        super(context);
    }

    public CompletableFuture<User> getUser(String userId) {
        CompletableFuture<User> future = new CompletableFuture<>();
        database.getReference("Users").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                User user = task.getResult().getValue(User.class);
                if (user != null) {
                    future.complete(user);
                } else {
                    future.completeExceptionally(new Exception("User not found"));
                }
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    public void addUser(User user) {
        if (user == null) {
            return;
        }
        database.getReference("Users").child(String.valueOf(user.getId())).setValue(user)
                .addOnFailureListener(e -> Log.e("Register", "Error saving user data"));
    }

    public void updateUser(User user) {
        if (user == null) {
            return;
        }
        database.getReference("Users").child(String.valueOf(user.getId())).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d("updateUser", "User updated successfully"))
                .addOnFailureListener(e -> Log.e("updateUser", "Failed to update user data", e));
    }
}
