package com.example.nexupnotes.reposetory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nexupnotes.model.Note
import com.example.nexupnotes.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Query

class NotesRepository{

    private val auth = FirebaseAuth.getInstance()
    private val userID = auth.currentUser?.uid!!

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    val notesCollection = db.collection(Constants.USERS).document(userID).collection("notes")


    fun addNote(
        title: String,
        content: String,
        onResult: (String) -> Unit
    ){
        val noteId = notesCollection.document().id
        val note = Note(noteId,title,content, System.currentTimeMillis(),false,userID)

        notesCollection.document(noteId).set(note)
            .addOnSuccessListener {
                onResult(noteId)
            }
            .addOnFailureListener {
                onResult("")
            }


    }




    fun updateNote(noteId: String, updateNote: Map<String, Any>, onResult: (Boolean) -> Unit){
        notesCollection
            .document(noteId)
            .set(updateNote, SetOptions.merge())
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun deleteNote(noteId: String, onResult: (Boolean)-> Unit){
        notesCollection
            .document(noteId)
            .delete()
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun restoreNote(note: Note, callback: (Boolean) -> Unit){
        notesCollection.document(note.noteId)
            .set(note)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getNewest() : LiveData<List<Note>>{
        val data = MutableLiveData<List<Note>>()
        notesCollection.orderBy("timestamp",  Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                value?.let {
                    data.postValue(it.toObjects(Note::class.java))
                }
            }
        return data
    }

    fun getOldest(): LiveData<List<Note>>{
        val data = MutableLiveData<List<Note>>()
        notesCollection.orderBy("timestamp",Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                value?.let {
                    data.postValue(it.toObjects(Note::class.java))
                }
            }
        return data
    }

    fun getSortByTitle() : LiveData<List<Note>>{
        val data = MutableLiveData<List<Note>>()
        notesCollection.orderBy("title",Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                value?.let {
                    data.postValue(it.toObjects(Note::class.java))
                }
            }
        return data
    }
}
