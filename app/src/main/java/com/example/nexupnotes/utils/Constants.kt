package com.example.nexupnotes.utils

import com.google.firebase.auth.FirebaseAuth

object Constants{
    const val USERS : String = "users"
    const val TITLE : String = "title"
    const val CONTENT : String = "content"
    const val NOTE_ID : String = "noteId"
    const val ERROR : String = "error"
    val USER_ID = FirebaseAuth.getInstance().currentUser?.uid!!


}