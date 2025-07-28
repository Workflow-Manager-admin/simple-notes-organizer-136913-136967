package com.example.androidfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidfrontend.model.Note;
import com.example.androidfrontend.repository.NoteRepository;

import java.util.Date;

/**
 * Activity for creating a new note or editing an existing one.
 */
// PUBLIC_INTERFACE
public class NewEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE_ID = "note_id";

    private EditText titleEditText, contentEditText;
    private NoteRepository noteRepo;
    private long noteId = -1;
    private Note existingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_NotesApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_note);

        noteRepo = new NoteRepository(this);
        titleEditText = findViewById(R.id.edit_note_title);
        contentEditText = findViewById(R.id.edit_note_content);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_NOTE_ID)) {
            noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);
            if (noteId != -1) {
                existingNote = noteRepo.getNoteById(noteId);
                if (existingNote != null) {
                    titleEditText.setText(existingNote.getTitle());
                    contentEditText.setText(existingNote.getContent());
                }
            }
        }
        findViewById(R.id.btn_save_note).setOnClickListener(view -> saveNote());
        findViewById(R.id.btn_cancel_note).setOnClickListener(view -> {
            finish();
        });

        // Up/home button in toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Date now = new Date();
        if (noteId != -1 && existingNote != null) {
            existingNote.setTitle(title);
            existingNote.setContent(content);
            existingNote.setUpdatedAt(now);
            noteRepo.updateNote(existingNote);
        } else {
            Note note = new Note(title, content, now, now);
            noteRepo.insertNote(note);
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Home/up button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
