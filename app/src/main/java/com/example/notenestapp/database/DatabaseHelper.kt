package com.example.notenestapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.notenestapp.models.Note
import com.example.notenestapp.models.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notenest.db"
        private const val DATABASE_VERSION = 1

        // Users table
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_SECURITY_QUESTION = "security_question"
        const val COLUMN_SECURITY_ANSWER = "security_answer"

        // Notes table
        const val TABLE_NOTES = "notes"
        const val COLUMN_NOTE_ID = "id"
        const val COLUMN_NOTE_USER_ID = "user_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_SUBJECT = "subject"
        const val COLUMN_COLOR = "color"
        const val COLUMN_IS_PINNED = "is_pinned"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_SECURITY_QUESTION + " TEXT,"
                + COLUMN_SECURITY_ANSWER + " TEXT" + ")")
        
        val createNotesTable = ("CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTE_USER_ID + " INTEGER,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_COLOR + " TEXT,"
                + COLUMN_IS_PINNED + " INTEGER,"
                + COLUMN_CREATED_AT + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_NOTE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))")

        db?.execSQL(createUsersTable)
        db?.execSQL(createNotesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    // User Operations
    fun addUser(fullName: String, email: String, passwordHash: String, question: String, answer: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, passwordHash)
        values.put(COLUMN_SECURITY_QUESTION, question)
        values.put(COLUMN_SECURITY_ANSWER, answer)
        return db.insert(TABLE_USERS, null, values)
    }

    fun checkUser(email: String, passwordHash: String): Int {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_USER_ID), "$COLUMN_EMAIL=? AND $COLUMN_PASSWORD=?", arrayOf(email, passwordHash), null, null, null)
        val id = if (cursor.moveToFirst()) cursor.getInt(0) else -1
        cursor.close()
        return id
    }

    fun getSecurityQuestion(email: String): String? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_SECURITY_QUESTION), "$COLUMN_EMAIL=?", arrayOf(email), null, null, null)
        val question = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        return question
    }

    fun verifySecurityAnswer(email: String, answer: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_USER_ID), "$COLUMN_EMAIL=? AND $COLUMN_SECURITY_ANSWER=?", arrayOf(email, answer), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun updatePassword(email: String, newPasswordHash: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PASSWORD, newPasswordHash)
        return db.update(TABLE_USERS, values, "$COLUMN_EMAIL=?", arrayOf(email))
    }

    fun getUserName(userId: Int): String {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_FULL_NAME), "$COLUMN_USER_ID=?", arrayOf(userId.toString()), null, null, null)
        val name = if (cursor.moveToFirst()) cursor.getString(0) else ""
        cursor.close()
        return name
    }

    // Note Operations
    fun addNote(note: Note): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE_USER_ID, note.userId)
        values.put(COLUMN_TITLE, note.title)
        values.put(COLUMN_CONTENT, note.content)
        values.put(COLUMN_SUBJECT, note.subject)
        values.put(COLUMN_COLOR, note.color)
        values.put(COLUMN_IS_PINNED, note.isPinned)
        values.put(COLUMN_CREATED_AT, note.createdAt)
        return db.insert(TABLE_NOTES, null, values)
    }

    fun updateNote(note: Note): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, note.title)
        values.put(COLUMN_CONTENT, note.content)
        values.put(COLUMN_SUBJECT, note.subject)
        values.put(COLUMN_COLOR, note.color)
        values.put(COLUMN_IS_PINNED, note.isPinned)
        return db.update(TABLE_NOTES, values, "$COLUMN_NOTE_ID=?", arrayOf(note.id.toString()))
    }

    fun deleteNote(noteId: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NOTES, "$COLUMN_NOTE_ID=?", arrayOf(noteId.toString()))
    }

    fun getNotes(userId: Int): ArrayList<Note> {
        val notesList = ArrayList<Note>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NOTES, null, "$COLUMN_NOTE_USER_ID=?", arrayOf(userId.toString()), null, null, "$COLUMN_CREATED_AT DESC")
        if (cursor.moveToFirst()) {
            do {
                notesList.add(Note(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_PINNED)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notesList
    }
}
