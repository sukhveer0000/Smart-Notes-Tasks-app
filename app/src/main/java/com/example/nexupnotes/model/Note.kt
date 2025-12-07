package com.example.nexupnotes.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Note(
    val noteId: String = "",
    val title: String ="",
    val content: String ="",
    val timestamp: Long= System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val userId : String = ""
){


}