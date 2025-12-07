package com.example.nexupnotes.reposetory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nexupnotes.model.Task
import com.example.nexupnotes.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject

class TasksRepository{
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid!!
    private val taskRef = db.collection(Constants.USERS).document(userId).collection("tasks")

    fun addTask(etTask: String,dueDate: Long? ,onResult: (Boolean) -> Unit){
        val taskId = taskRef.document().id
        val task = Task(taskId,etTask,false,System.currentTimeMillis(),dueDate)
        taskRef.document(taskId)
            .set(task)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getTask() : LiveData<List<Task>> {
        val data = MutableLiveData<List<Task>>()
        taskRef
            .addSnapshotListener { value, error ->
                value?.let {
                    data.postValue(it.toObjects(Task::class.java))
                }
            }
        return data
    }

    fun setComplete(isChecked: Boolean,task: Task,onResult: (Boolean) -> Unit){
        val isCompleted = mapOf<String, Boolean>(
            "completed" to isChecked)
        taskRef.document(task.taskId)
            .set(isCompleted, SetOptions.merge())
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun updateTask(editTask: String,newDueDate: Long?, taskId: String){
        val note = mapOf<String, Any?>(
            "title" to editTask,
            "dueDate" to newDueDate,
            "timestamp" to System.currentTimeMillis()
        )
        taskRef.document(taskId)
            .set(note, SetOptions.merge())
    }

    fun updateTaskSelected(task: Task){
        taskRef.document(task.taskId)
            .update("selected" , task.selected)
    }

    fun deletedTask(task : Task){
        taskRef.document(task.taskId)
            .delete()
    }
}
