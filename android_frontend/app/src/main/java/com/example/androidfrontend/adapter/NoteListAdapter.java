package com.example.androidfrontend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfrontend.R;
import com.example.androidfrontend.model.Note;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying notes in RecyclerView.
 */
// PUBLIC_INTERFACE
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteLongClick(Note note);
    }

    private List<Note> notes;
    private OnNoteClickListener listener;

    public NoteListAdapter(List<Note> notes, OnNoteClickListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note n = notes.get(position);
        holder.title.setText(n.getTitle());
        holder.content.setText(n.getContent().length() > 128 ? n.getContent().substring(0, 127) + "..." : n.getContent());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        holder.date.setText(sdf.format(n.getCreatedAt()));

        holder.itemView.setOnClickListener(v -> listener.onNoteClick(n));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onNoteLongClick(n);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // PUBLIC_INTERFACE
    /**
     * Set new data for the adapter.
     */
    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content, date;
        public NoteViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            content = itemView.findViewById(R.id.note_content);
            date = itemView.findViewById(R.id.note_date);
        }
    }
}
