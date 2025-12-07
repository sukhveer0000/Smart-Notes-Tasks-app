package com.example.nexupnotes.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nexupnotes.model.Task
import com.example.nexupnotes.reposetory.TasksRepository
import com.example.nexupnotes.utils.Constants
import javax.security.auth.callback.Callback

class TasksViewModel : ViewModel(){
    private val repository = TasksRepository()

    //selection Mode
    private val _selectionMode = MutableLiveData<Boolean>()
    val selectionMode : LiveData<Boolean> get() = _selectionMode

    fun enableSelectionMode(){
        _selectionMode.value = true
    }

    fun disableSelectionMode(){
        _selectionMode.value = false
    }

    //count Selected
    private val _selectedCount = MutableLiveData(0)
    val selectedCount: LiveData<Int> get() = _selectedCount

    fun updateSelected(count: Int){
        _selectedCount.value = count
    }

    private val _taskList = MutableLiveData<List<Task>>()
    val taskList : LiveData<List<Task>> get() = _taskList

    fun addTask(task:String, dueDate:Long? ){
        repository.addTask(task,dueDate) {result->
            Log.d(Constants.ERROR,result.toString())
        }
    }

    fun getTask() {
        repository.getTask().observeForever {
            _taskList.value = it
        }
    }
    fun setCompleteStatus(isCompleted: Boolean,task: Task){
        repository.setComplete(isCompleted,task){ result ->
            Log.d("complete",result.toString())
        }
    }

    fun updateTask(title: String,dueDate: Long?,taskId: String){
        repository.updateTask(title,dueDate,taskId)
        getTask()
    }

    fun selectTask(select: Boolean,task: Task){
        task.selected = select
        repository.updateTaskSelected(task)
    }

    fun selectAll(tasks: List<Task>){
        tasks.forEach {
            it.selected = true
            repository.updateTaskSelected(it)
        }
        updateSelected(tasks.size)
    }
    fun deselectAll(tasks: List<Task>){
        tasks.forEach {
            it.selected = false
            repository.updateTaskSelected(it)
        }
        updateSelected(0)
    }

    fun deleteSelectedTasks(selectedTasks: List<Task>){
        selectedTasks.forEach {
            repository.deletedTask(it)
        }
    }
}