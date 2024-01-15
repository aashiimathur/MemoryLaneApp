package com.example.journalapp;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<Journal> journalList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Journal journal);
        void onDeleteClick(Journal journal);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MyAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Binding data to views for a specific item
        Journal currentJournal = journalList.get(position);

        holder.title.setText(currentJournal.getTitle());
        holder.thoughts.setText(currentJournal.getThoughts());
        holder.name.setText(currentJournal.getUserName());
        holder.setDocumentId(currentJournal.getDocumentId()); // Set the document ID

        String imageUrl = currentJournal.getImageUrl();

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(
                currentJournal.getTimeAdded().getSeconds() * 1000
        );
        holder.dateAdded.setText(timeAgo);

        // Glide Library to display the image
        Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .into(holder.image);

        holder.editButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.onEditClick(currentJournal);
            }
        });

        holder.deleteButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeleteClick(currentJournal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return journalList.size(); //returns the total number of items in the dataset.
    }

    // View Holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, thoughts, dateAdded, name;
        public ImageView image;
        public ImageButton editButton, deleteButton;
        private String documentId; // Add documentId field

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.journal_title_list);
            thoughts = itemView.findViewById(R.id.journal_thought_list);
            dateAdded = itemView.findViewById(R.id.journal_timestamp_list);
            name = itemView.findViewById(R.id.journal_row_username);
            image = itemView.findViewById(R.id.journal_image_list);
            editButton = itemView.findViewById(R.id.journal_row_edit_button);
            deleteButton = itemView.findViewById(R.id.journal_row_delete_button);
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId; //Setter for document id
        }
    }
}
