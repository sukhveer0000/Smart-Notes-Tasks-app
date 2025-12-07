package com.example.nexupnotes.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nexupnotes.R
import com.example.nexupnotes.adapters.NotesAdapter
import com.example.nexupnotes.databinding.FragmentNotesBinding
import com.example.nexupnotes.home.addnote.AddNoteActivity
import com.example.nexupnotes.model.Note
import com.example.nexupnotes.utils.Constants
import com.example.nexupnotes.viewmodel.NotesViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class NotesFragment : Fragment() {

    private var _binding : FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel
    private val userId = FirebaseAuth.getInstance().uid!!
    private var isGrid = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotesBinding.inflate(inflater,container,false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        sharedPreferences = requireContext().getSharedPreferences("sharp", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        viewModel = ViewModelProvider(this)[NotesViewModel::class.java]
        viewModel.getNotes()

        val adapter = NotesAdapter(requireContext(), mutableListOf()){ note ->
            Intent(requireContext(), AddNoteActivity::class.java).also {
                it.putExtra(Constants.NOTE_ID,note.noteId)
                it.putExtra(Constants.TITLE,note.title)
                it.putExtra(Constants.CONTENT,note.content)
                startActivity(it)
            }
        }

        binding.rvNote.layoutManager = GridLayoutManager(requireContext(),1)
        binding.rvNote.setHasFixedSize(true)
        binding.rvNote.adapter = adapter

        viewModel.notesList.observe(viewLifecycleOwner){ notes ->
            adapter.update(notes)
        }

        binding.addNote.setOnClickListener {
            startActivity(Intent(requireActivity(), AddNoteActivity::class.java))
        }

        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                try{
                    val currentNote = viewHolder.adapterPosition
                    val note = adapter.notesList[currentNote]
                    viewModel.deleteNote(note.noteId, userId)

                    Snackbar.make(binding.rvNote, "Note Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            viewModel.restoreNote(note)
                        }.show()
                }catch (e: Exception){
                    Log.d("error",e.toString())
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                    .addActionIcon(R.drawable.outline_delete_24)
                    .create()
                    .decorate()
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        })

        itemTouchHelper.attachToRecyclerView(binding.rvNote)

        // menu provider
        requireActivity().addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menuInflater.inflate(R.menu.toolbar_menu,menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.grid ->{
                        isGrid = !isGrid
                        editor.putBoolean("isGrid",isGrid)
                        editor.apply()
                        updateLayout()
                        menuItem.title = if (isGrid) "List view" else "Grid view"
                        return true
                    }
                    R.id.shor_by_time ->{
                        viewModel.sortByTime()
                        editor.putString("sort","newest")
                        editor.apply()
                        return true
                    }
                    R.id.oldest ->{
                        viewModel.getOldest()
                        editor.putString("sort","oldest")
                        editor.apply()
                        return true
                    }
                    R.id.shor_by_title ->{
                        viewModel.sortByTitle()
                        editor.putString("sort","title").apply()
                        return true
                    }
                    else -> false
                }
            }
        },viewLifecycleOwner)


        return binding.root
    }
    private fun updateLayout() {
        if (isGrid){
            binding.rvNote.layoutManager = GridLayoutManager(requireContext(),2)
        }
        else {
            binding.rvNote.layoutManager = GridLayoutManager(requireContext(),1)
        }
    }

    override fun onResume() {
        super.onResume()

        val isGrid = sharedPreferences.getBoolean("isGrid",false)
        if (isGrid){
            binding.rvNote.layoutManager = GridLayoutManager(requireContext(),2)
        }
        else {
            binding.rvNote.layoutManager = GridLayoutManager(requireContext(),1)
        }

        val sortType = sharedPreferences.getString("sort","newest")
        when{
            (sortType == "oldest") -> viewModel.getOldest()
            (sortType == "newest") -> viewModel.sortByTime()
            (sortType == "title") -> viewModel.sortByTitle()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}


