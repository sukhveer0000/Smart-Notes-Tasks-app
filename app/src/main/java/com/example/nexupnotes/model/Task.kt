package com.example.nexupnotes.model

import com.google.firebase.firestore.Exclude

data class Task(
    val taskId: String = "",
    val title: String = "",
    val completed : Boolean = false,
    val timestamp : Long = System.currentTimeMillis(),
    val dueDate : Long? = null,
    var selected: Boolean = false,
    val priority : String = "normal"
)
