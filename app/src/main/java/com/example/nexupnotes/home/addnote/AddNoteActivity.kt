package com.example.nexupnotes.home.addnote

import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nexupnotes.R
import com.example.nexupnotes.databinding.ActivityAddNoteBinding
import com.example.nexupnotes.databinding.ActivityMainBinding
import com.example.nexupnotes.databinding.ActivityMainBinding.*
import com.example.nexupnotes.model.Note
import com.example.nexupnotes.utils.Constants
import com.example.nexupnotes.viewmodel.NotesViewModel

class AddNoteActivity : AppCompatActivity() {


    private lateinit var viewModel: NotesViewModel
    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[NotesViewModel::class.java]
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var noteId = intent.getStringExtra(Constants.NOTE_ID) ?: ""

        if (!noteId.isEmpty()) {
            val title = intent.getStringExtra(Constants.TITLE) ?: ""
            binding.etTitle.setText(title)
            val content = intent.getStringExtra(Constants.CONTENT) ?: ""
            binding.etContent.setText(content)
        }


        binding.btnSaveNote.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()
            if(noteId.isEmpty()) {
                viewModel.addNote(title, content){ noteID ->
                    noteId = noteID
                }
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
            }
            else{
                val note = mapOf<String, Any>(
                    "title" to title,
                    "content" to content,
                    "timestamp" to System.currentTimeMillis()
                )
                viewModel.updateNote(noteId, note)
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()

            }
        }

    }
}