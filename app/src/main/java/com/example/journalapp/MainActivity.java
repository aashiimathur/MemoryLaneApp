package com.example.journalapp;



import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class MainActivity extends AppCompatActivity {

    // Widgets
    private Button loginBtn, createAccountBtn;
    private EditText emailEt, passEt;

    // Firebase Auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createAccountBtn = findViewById(R.id.create_account);

        createAccountBtn.setOnClickListener(v -> {
            // OnClick()
            Intent i = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(i);
        });
        // Login
        loginBtn = findViewById(R.id.email_signin);
        emailEt = findViewById(R.id.email);
        passEt = findViewById(R.id.password);

        // Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        // If the user is already logged in, redirect to JournalListActivity
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(MainActivity.this, JournalListActivity.class);
            startActivity(i);
            finish();
        }

        emailEt = findViewById(R.id.email);
        passEt = findViewById(R.id.password);

        Button loginBtn = findViewById(R.id.email_signin);
        loginBtn.setOnClickListener(v -> loginEmailPassUser(
                emailEt.getText().toString().trim(),
                passEt.getText().toString().trim()));
        createNotificationChannel();
    }

    private void loginEmailPassUser(String email, String pwd) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // User has logged in successfully
                            showWelcomeNotification();
                            Intent i = new Intent(MainActivity.this, JournalListActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(MainActivity.this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWelcomeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "savePosts")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Welcome back!")
                .setContentText("You have successfully logged in.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        notificationManager.notify(1, builder.build());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "YourChannelName";
            String description = "YourChannelDescription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Posts", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}