package com.example.notenestapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notenestapp.database.DatabaseHelper
import com.example.notenestapp.databinding.ActivityNoteDetailBinding
import com.example.notenestapp.models.Note

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailBinding
    private lateinit var dbHelper: DatabaseHelper
    private var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        note = intent.getSerializableExtra("NOTE") as? Note

        note?.let { displayNote(it) }

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.fabEdit.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra("NOTE", note)
            startActivity(intent)
            finish()
        }

        binding.fabDelete.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun displayNote(note: Note) {
        binding.tvTitle.text = note.title
        binding.tvSubject.text = note.subject
        binding.tvDate.text = note.createdAt
        binding.tvContent.text = note.content
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                note?.let {
                    dbHelper.deleteNote(it.id)
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
