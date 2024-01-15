package com.example.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpActivity extends AppCompatActivity { //class declaration

    // Widgets
    EditText passwordEditText, usernameEditText, emailEditText;
    Button signUpButton;

    // Firebase Authentication
    private FirebaseAuth firebaseAuth;

    // Firebase Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersCollection = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize widgets
        emailEditText = findViewById(R.id.email_create);
        passwordEditText = findViewById(R.id.password_create);
        usernameEditText = findViewById(R.id.username_create_ET);
        signUpButton = findViewById(R.id.acc_signUp_btn);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserWithEmailAndPassword();
            }
        });
    }

    private void createUserWithEmailAndPassword() {
        //Get user input
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        //Validate input
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
            Toast.makeText(this, "No empty fields allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        //Create user with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Account creation success
                            Toast.makeText(SignUpActivity.this,
                                    "Account created successfully. Kindly login.",
                                    Toast.LENGTH_SHORT).show();

                            // Add the user to the Users collection
                            addUserToCollection(email, username);
                            // Redirect the user to the main activity
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish(); // Close the sign-up activity

                        } else {
                            // Account creation failed
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(SignUpActivity.this,
                                    "This account already exists",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToCollection(String email, String username) {
        // Add the user to the Users collection (example)
        User user = new User(email, username);
        usersCollection.add(user);
    }


    class User {
        private String email;
        private String username;

        public User(String email, String username) {
            this.email = email;
            this.username = username;
        }
    }
}
