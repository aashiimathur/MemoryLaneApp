package com.example.journalapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
public class EditJournalActivity extends AppCompatActivity {

    private EditText editTitleEditText;
    private EditText editThoughtsEditText;
    private Button updateButton;
    private Journal journalToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal);
        //Initialize views
        editTitleEditText = findViewById(R.id.edit_post_title_et);
        editThoughtsEditText = findViewById(R.id.edit_post_description_et);
        updateButton = findViewById(R.id.edit_post_update_button);

        // Get the journal object from the intent
        journalToUpdate = getIntent().getParcelableExtra("journal");

        if (journalToUpdate != null) {
            // Set existing values to the EditText fields
            editTitleEditText.setText(journalToUpdate.getTitle());
            editThoughtsEditText.setText(journalToUpdate.getThoughts());
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the update button click
                updateJournal();
            }
        });
    }

    private void updateJournal() {
        // Check if journalToUpdate is not null
        if (journalToUpdate != null) {
            String updatedTitle = editTitleEditText.getText().toString().trim();
            String updatedThoughts = editThoughtsEditText.getText().toString().trim();
            // Check if title and thoughts are not empty
            if (!TextUtils.isEmpty(updatedTitle) && !TextUtils.isEmpty(updatedThoughts)) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Journal")
                        .document(journalToUpdate.getDocumentId())
                        .update("title", updatedTitle, "thoughts", updatedThoughts)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditJournalActivity.this, "Post updated", Toast.LENGTH_SHORT).show();
                                finish(); //close the activity
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditJournalActivity.this, "Failed to update post", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Title and Thoughts cannot be empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("UpdateJournal", "JournalToUpdate is null");
        }
    }


}
