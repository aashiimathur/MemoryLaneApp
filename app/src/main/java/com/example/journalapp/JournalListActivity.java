package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class JournalListActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    // Firebase Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journal");

    // List of Journals
    private List<Journal> journalList;

    // RecyclerView
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    // Widgets
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        // Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        // Widgets
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Posts ArrayList
        journalList = new ArrayList<>();

        // Initialize the adapter
        myAdapter = new MyAdapter(JournalListActivity.this, journalList);
        myAdapter.setOnItemClickListener(this); // Set the click listener
        recyclerView.setAdapter(myAdapter);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JournalListActivity.this, AddJournalActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Display community posts by default
        displayCommunityPosts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_add) {
            // Code to handle adding a new post
            Intent i = new Intent(JournalListActivity.this, AddJournalActivity.class);
            startActivity(i);
            return true;
        } else if (itemId == R.id.action_signout) {
            // Code to handle signing out
            if (user != null && firebaseAuth != null) {
                firebaseAuth.signOut();
                Intent i = new Intent(JournalListActivity.this, MainActivity.class);
                startActivity(i);
                finish(); //finish the current activity after signing out
                return true;
            }
        } else if (itemId == R.id.action_community_posts) {
            // Code to display community posts
            displayCommunityPosts();
            return true;
        } else if (itemId == R.id.action_my_posts) {
            // Code to display the user's posts
            displayMyPosts();
            return true;
        }

        return super.onOptionsItemSelected(item);
        }

    private void displayCommunityPosts() {
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            journalList.clear(); // Clear existing data
            for (QueryDocumentSnapshot journals : queryDocumentSnapshots) {
                Journal journal = journals.toObject(Journal.class);
                journal.setDocumentId(journals.getId());
                journalList.add(journal);
            }
            myAdapter.notifyDataSetChanged();
            showToast("Displaying Community Posts");

        }).addOnFailureListener(e -> showToast("Failed to retrieve community posts: " + e.getMessage()));
    }

    private void displayMyPosts() {
        if (user != null) {
            collectionReference.whereEqualTo("userId", user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        journalList.clear(); // Clear existing data
                        for (QueryDocumentSnapshot journals : queryDocumentSnapshots) {
                            Journal journal = journals.toObject(Journal.class);
                            journalList.add(journal);
                        }
                        myAdapter.notifyDataSetChanged();
                        showToast("Displaying My Posts");

                    })
                    .addOnFailureListener(e -> showToast("Failed to retrieve your posts: " + e.getMessage()));
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Journal journal) {
        // Check if the current user is the owner of the post
        if (user != null && journal.getUserId() != null && user.getUid().equals(journal.getUserId())) {
            // Allow editing
            Intent editIntent = new Intent(this, EditJournalActivity.class);
            editIntent.putExtra("journal", journal);
            startActivity(editIntent);
        } else {
            // Display a message or handle the case where the user is not the owner
            Toast.makeText(this, "You can only edit your own posts", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(Journal journal) {
        // Check if the current user is the owner of the post
        if (user != null && journal.getUserId() != null && user.getUid().equals(journal.getUserId())) {
            // Allow deletion
            String documentId = journal.getDocumentId();
            collectionReference.document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        showNotification("Memory Lane", "Post deleted successfully.");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(JournalListActivity.this, "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Display a message or handle the case where the user is not the owner
            Toast.makeText(this, "You can only delete your own posts", Toast.LENGTH_SHORT).show();
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
            return;
        }
        notificationManager.notify(1, builder.build()); // Use a unique notification ID
    }

}

