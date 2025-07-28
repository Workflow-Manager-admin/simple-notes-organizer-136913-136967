package com.example.androidfrontend.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.androidfrontend.database.NoteDbHelper;
import com.example.androidfrontend.model.Note;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repository for Notes - handles all database CRUD and query operations.
 */
public class NoteRepository {
    private NoteDbHelper dbHelper;

    public NoteRepository(Context context) {
        dbHelper = new NoteDbHelper(context);
    }

    // PUBLIC_INTERFACE
    /**
     * Create a new note.
     */
    public long insertNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteDbHelper.COLUMN_TITLE, note.getTitle());
        values.put(NoteDbHelper.COLUMN_CONTENT, note.getContent());
        values.put(NoteDbHelper.COLUMN_CREATED_AT, note.getCreatedAt().getTime());
        values.put(NoteDbHelper.COLUMN_UPDATED_AT, note.getUpdatedAt().getTime());
        return db.insert(NoteDbHelper.TABLE_NOTES, null, values);
    }

    // PUBLIC_INTERFACE
    /**
     * Get all notes, optionally filtered by search query and/or date.
     */
    public List<Note> getNotes(String searchQuery, Long dateMillis) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Note> notes = new ArrayList<>();
        String selection = "";
        List<String> selectionArgsList = new ArrayList<>();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            selection += NoteDbHelper.COLUMN_TITLE + " LIKE ? OR " + NoteDbHelper.COLUMN_CONTENT + " LIKE ?";
            selectionArgsList.add("%" + searchQuery + "%");
            selectionArgsList.add("%" + searchQuery + "%");
        }

        if (dateMillis != null) {
            if (!selection.isEmpty()) selection += " AND ";
            // Organize notes created on the same date (by day)
            long startOfDay = dateMillis - (dateMillis % (24 * 60 * 60 * 1000));
            long endOfDay = startOfDay + (24 * 60 * 60 * 1000);
            selection += NoteDbHelper.COLUMN_CREATED_AT + " >= ? AND " + NoteDbHelper.COLUMN_CREATED_AT + " < ?";
            selectionArgsList.add(String.valueOf(startOfDay));
            selectionArgsList.add(String.valueOf(endOfDay));
        }
        String[] selectionArgs = selectionArgsList.toArray(new String[0]);

        Cursor cursor = db.query(
            NoteDbHelper.TABLE_NOTES, null,
            selection.isEmpty() ? null : selection,
            selectionArgs.length == 0 ? null : selectionArgs,
            null, null,
            NoteDbHelper.COLUMN_CREATED_AT + " DESC"
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_CONTENT));
            long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_CREATED_AT));
            long updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_UPDATED_AT));
            notes.add(new Note(id, title, content, new Date(createdAt), new Date(updatedAt)));
        }
        cursor.close();
        return notes;
    }

    // PUBLIC_INTERFACE
    /**
     * Get a note by its ID.
     */
    public Note getNoteById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            NoteDbHelper.TABLE_NOTES, null,
            NoteDbHelper.COLUMN_ID + " = ?",
            new String[]{String.valueOf(id)},
            null, null, null
        );
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_CONTENT));
            long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_CREATED_AT));
            long updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_UPDATED_AT));
            Note note = new Note(id, title, content, new Date(createdAt), new Date(updatedAt));
            cursor.close();
            return note;
        } else {
            cursor.close();
            return null;
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Update an existing note.
     */
    public int updateNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteDbHelper.COLUMN_TITLE, note.getTitle());
        values.put(NoteDbHelper.COLUMN_CONTENT, note.getContent());
        values.put(NoteDbHelper.COLUMN_UPDATED_AT, note.getUpdatedAt().getTime());
        return db.update(NoteDbHelper.TABLE_NOTES, values, NoteDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(note.getId())});
    }

    // PUBLIC_INTERFACE
    /**
     * Delete a note by its ID.
     */
    public int deleteNote(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(NoteDbHelper.TABLE_NOTES, NoteDbHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
}
