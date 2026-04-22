package com.example.notenestapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notenestapp.adapters.NotesAdapter
import com.example.notenestapp.database.DatabaseHelper
import com.example.notenestapp.databinding.ActivityHomeBinding
import com.example.notenestapp.models.Note
import com.example.notenestapp.utils.SessionManager
import com.google.android.material.chip.Chip

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var notesAdapter: NotesAdapter
    private var notesList = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        setupRecyclerView()
        loadNotes()
        setupChips()

        binding.fabAddNote.setOnClickListener {
            startActivity(Intent(this, AddEditNoteActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterNotes(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(ArrayList()) { note ->
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra("NOTE", note)
            startActivity(intent)
        }
        binding.rvNotes.layoutManager = GridLayoutManager(this, 2)
        binding.rvNotes.adapter = notesAdapter
    }

    private fun loadNotes() {
        notesList = dbHelper.getNotes(sessionManager.getUserId())
        notesAdapter.updateList(notesList)
        updateChips()
    }

    private fun updateChips() {
        val subjects = notesList.map { it.subject }.distinct()
        binding.chipGroupSubjects.removeAllViews()
        
        val allChip = Chip(this)
        allChip.text = "All"
        allChip.isCheckable = true
        allChip.isChecked = true
        allChip.setOnClickListener { filterNotes("") }
        binding.chipGroupSubjects.addView(allChip)

        subjects.forEach { subject ->
            val chip = Chip(this)
            chip.text = subject
            chip.isCheckable = true
            chip.setOnClickListener { filterBySubject(subject) }
            binding.chipGroupSubjects.addView(chip)
        }
    }

    private fun setupChips() {
        // Initial setup if needed
    }

    private fun filterNotes(query: String) {
        val filtered = notesList.filter { 
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true) 
        }
        notesAdapter.updateList(filtered)
    }

    private fun filterBySubject(subject: String) {
        val filtered = notesList.filter { it.subject == subject }
        notesAdapter.updateList(filtered)
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }
}
