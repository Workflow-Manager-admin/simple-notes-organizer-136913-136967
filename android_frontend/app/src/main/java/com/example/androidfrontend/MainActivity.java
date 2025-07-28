package com.example.androidfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfrontend.adapter.NoteListAdapter;
import com.example.androidfrontend.model.Note;
import com.example.androidfrontend.repository.NoteRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;
import java.util.List;
import java.util.Calendar;

/**
 * Main activity for displaying list of notes.
 */
// PUBLIC_INTERFACE
public class MainActivity extends AppCompatActivity implements
        NoteListAdapter.OnNoteClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView notesListView;
    private NoteListAdapter noteListAdapter;
    private NoteRepository noteRepo;
    private FloatingActionButton fab;
    private SearchView searchView;
    private NavigationView navigationView;

    private String searchQuery = "";
    private Long filterByDateMillis = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_NotesApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        noteRepo = new NoteRepository(this);
        notesListView = findViewById(R.id.notes_list);
        notesListView.setLayoutManager(new LinearLayoutManager(this));
        noteListAdapter = new NoteListAdapter(noteRepo.getNotes(searchQuery, filterByDateMillis), this);
        notesListView.setAdapter(noteListAdapter);

        fab = findViewById(R.id.fab_new_note);
        fab.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, NewEditNoteActivity.class);
            startActivity(i);
        });

        searchView = findViewById(R.id.search_notes);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) { return false; }
            @Override
            public boolean onQueryTextChange(String q) {
                searchQuery = q;
                reloadNotes();
                return true;
            }
        });

        // Start with all notes loaded
        reloadNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadNotes();
    }

    // PUBLIC_INTERFACE
    /**
     * Invoked when a note is tapped (edit).
     */
    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(this, NewEditNoteActivity.class);
        intent.putExtra(NewEditNoteActivity.EXTRA_NOTE_ID, note.getId());
        startActivity(intent);
    }

    // PUBLIC_INTERFACE
    /**
     * Invoked when a note is long-pressed (delete).
     */
    @Override
    public void onNoteLongClick(Note note) {
        noteRepo.deleteNote(note.getId());
        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
        reloadNotes();
    }

    private void reloadNotes() {
        List<Note> filteredNotes = noteRepo.getNotes(searchQuery, filterByDateMillis);
        noteListAdapter.setNotes(filteredNotes);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.menu_all_notes) {
            filterByDateMillis = null;
        } else if (id == R.id.menu_today_notes) {
            // Set filter to only show today's date notes
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            filterByDateMillis = calendar.getTimeInMillis();
        } // Settings can be expanded as needed
        reloadNotes();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
