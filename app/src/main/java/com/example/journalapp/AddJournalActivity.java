package com.example.journalapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText titleEditText;
    private EditText thoughtsEditText;
    private ImageView imageView;
    private Button saveButton;
    private ImageView addPhotoBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journal");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private String currentUserId;
    private String currentUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private ActivityResultLauncher<String> mTakePhoto; // Activity Result Launcher for taking a photo
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the layout
        setContentView(R.layout.activity_add_journal);
        //initializing variables
        progressBar = findViewById(R.id.post_progressBar);
        titleEditText = findViewById(R.id.post_title_et);
        thoughtsEditText = findViewById(R.id.post_description_et);
        imageView = findViewById(R.id.post_imageView);
        saveButton = findViewById(R.id.post_save_journal_button);
        addPhotoBtn = findViewById(R.id.postCameraButton);

        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        saveButton.setOnClickListener(v -> saveJournal());

        mTakePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        //Set the captured image to the ImageView
                        imageView.setImageURI(result);
                        imageUri = result;
                    }
                }
        );

        addPhotoBtn.setOnClickListener(v -> mTakePhoto.launch("image/*"));//launches the photo capture process
    }

    private void saveJournal() {
        String title = titleEditText.getText().toString().trim();
        String thoughts = thoughtsEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null) {
            // Get the current user
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // Get the current user ID
                currentUserId = currentUser.getUid();

                // The saving path of the images in Firebase Storage:
                // ........./journal_images/my_image_202310071430.png
                final StorageReference filePath = storageReference.child("journal_images")
                        .child("my_image_" + Timestamp.now().getSeconds());

                // Uploading the image
                filePath.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();

                                // Creating a Journal Object
                                Journal journal = new Journal();
                                journal.setTitle(title);
                                journal.setThoughts(thoughts);
                                journal.setImageUrl(imageUrl);

                                // Set the document ID while creating the journal
                                String documentId = collectionReference.document().getId();
                                journal.setDocumentId(documentId);

                                journal.setTimeAdded(new Timestamp(new Date()));
                                journal.setUserName(currentUserName);
                                journal.setUserId(currentUserId);

                                // Add the journal to Firestore
                                collectionReference.document(documentId)
                                        .set(journal)
                                        .addOnSuccessListener(aVoid -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            showNotification("Post Saved", "Your post has been uploaded!");

                                            Intent i = new Intent(AddJournalActivity.this, JournalListActivity.class);
                                            startActivity(i);
                                            finish();

                                        })
                                        .addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(AddJournalActivity.this, "Failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                            });
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddJournalActivity.this, "Failed !!!!", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Handle the case where the user is not authenticated
                progressBar.setVisibility(View.INVISIBLE);
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Title, Thoughts, and Image cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Posts")
                .setSmallIcon(R.drawable.logo) // Set your notification icon
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build()); // Use a unique notification ID
    }

}
