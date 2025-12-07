package com.example.nexupnotes.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nexupnotes.model.Note
import com.example.nexupnotes.reposetory.NotesRepository
import com.google.firebase.auth.FirebaseAuth

class NotesViewModel : ViewModel(){

    // To get userId for current User
    private val auth = FirebaseAuth.getInstance()
    private val _userId = auth.currentUser?.uid!!
    private val userId get() = _userId

    private val repository = NotesRepository()

    private val _sortType = MutableLiveData(SortType.NEWEST)
    val sortType : LiveData<SortType> get() = _sortType

    private val _notesList = MutableLiveData<List<Note>>()
    val notesList : LiveData<List<Note>> get() = _notesList

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult : LiveData<Boolean> get() =_operationResult

    fun getNotes(){
        repository.getNewest().observeForever {
            _notesList.value = it
        }
    }


    // Working code for now
    fun addNote(title: String, content: String, callback: (String) -> Unit){
        repository.addNote(title,content){ noteId ->
            if(noteId.isNotEmpty()){
                _operationResult.value = true
                getNotes()
                callback(noteId)
            }else{
                _operationResult.value = false
                callback("")
            }
        }
    }

    fun updateNote(noteId: String, note: Map<String, Any>){
        repository.updateNote(noteId,note){success ->
            _operationResult.value = success
            if(success) getNotes()
        }
    }

    fun deleteNote(noteId: String, userId: String){
        repository.deleteNote(noteId){ success ->
            _operationResult.value = success
            if (success) getNotes()
        }
    }
    fun restoreNote(note: Note){
        repository.restoreNote(note) { success ->
            _operationResult.value = success
            if(success) getNotes()
        }
    }

    fun sortByTime(){
        repository.getNewest().observeForever {
            _notesList.value = it
        }
    }
    fun getOldest(){
        repository.getOldest().observeForever {
            _notesList.value = it
        }
    }

    fun sortByTitle() {
        repository.getSortByTitle().observeForever {
            _notesList.value = it
        }
    }

    enum class SortType{
        NEWEST, OLDEST, TITLE
    }

}