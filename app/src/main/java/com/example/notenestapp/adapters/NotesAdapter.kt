package com.example.notenestapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notenestapp.R
import com.example.notenestapp.models.Note
import com.google.android.material.card.MaterialCardView

class NotesAdapter(
    private var notes: List<Note>,
    private val onNoteClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    fun updateList(newList: List<Note>) {
        notes = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardNote: MaterialCardView = itemView.findViewById(R.id.cardNote)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(note: Note) {
            tvTitle.text = note.title
            tvContent.text = note.content
            tvSubject.text = note.subject
            tvDate.text = note.createdAt

            try {
                cardNote.setCardBackgroundColor(Color.parseColor(note.color))
            } catch (e: Exception) {
                cardNote.setCardBackgroundColor(Color.WHITE)
            }

            // Set click listener on the CardView itself instead of itemView
            cardNote.setOnClickListener { onNoteClick(note) }
        }
    }
}
