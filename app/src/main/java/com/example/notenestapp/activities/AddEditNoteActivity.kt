package com.example.notenestapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notenestapp.database.DatabaseHelper
import com.example.notenestapp.databinding.ActivityAddEditNoteBinding
import com.example.notenestapp.models.Note
import com.example.notenestapp.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditNoteBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private var existingNote: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        existingNote = intent.getSerializableExtra("NOTE") as? Note

        if (existingNote != null) {
            binding.etTitle.setText(existingNote!!.title)
            binding.etContent.setText(existingNote!!.content)
            binding.etSubject.setText(existingNote!!.subject)
            binding.etColor.setText(existingNote!!.color)
            binding.btnSave.text = "Update Note"
        }

        binding.btnSave.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        val subject = binding.etSubject.text.toString().trim()
        val color = binding.etColor.text.toString().trim()

        if (title.isEmpty() || content.isEmpty() || subject.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val currentDate = sdf.format(Date())

        if (existingNote == null) {
            val newNote = Note(
                userId = sessionManager.getUserId(),
                title = title,
                content = content,
                subject = subject,
                color = if (color.isEmpty()) "#FFFFFF" else color,
                createdAt = currentDate
            )
            dbHelper.addNote(newNote)
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
        } else {
            val updatedNote = existingNote!!.copy(
                title = title,
                content = content,
                subject = subject,
                color = if (color.isEmpty()) "#FFFFFF" else color
            )
            dbHelper.updateNote(updatedNote)
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
