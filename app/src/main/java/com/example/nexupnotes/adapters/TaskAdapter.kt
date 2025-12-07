package com.example.nexupnotes.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.example.nexupnotes.R
import com.example.nexupnotes.home.TasksFragment
import com.example.nexupnotes.model.Task
import com.example.nexupnotes.viewmodel.TasksViewModel
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.min


@SuppressLint("NotifyDataSetChanged")
class TaskAdapter(
    val viewModel: TasksViewModel,
    val taskList: MutableList<Task>,
    val itemOnclick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    var isSelectMode = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_card_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val currentItem = taskList[position]
        holder.taskText.text = currentItem.title
        if(currentItem.dueDate == null){
            holder.dueDate.visibility = View.GONE
        }else{
            val dueDateText = getDueDate(currentItem.dueDate)
            holder.dueDate.text = dueDateText
        }

        if (isSelectMode){
            holder.select.visibility = View.VISIBLE
            holder.checkBox.visibility = View.GONE
            holder.select.setOnCheckedChangeListener(null)
            holder.select.isChecked = currentItem.selected
            holder.select.setOnCheckedChangeListener {_,isChecked ->
                currentItem.selected = isChecked
                viewModel.updateSelected(selectedCount())
                viewModel.selectTask(isChecked,currentItem)
            }
            holder.itemView.setOnClickListener {
                currentItem.selected = !currentItem.selected
                holder.select.isChecked = currentItem.selected
                viewModel.updateSelected(selectedCount())
                viewModel.selectTask(currentItem.selected,currentItem)
            }
        }else {
            holder.select.visibility = View.GONE
            holder.checkBox.visibility = View.VISIBLE

            holder.itemView.setOnClickListener {
                val task = taskList[holder.adapterPosition]
                itemOnclick(task)
            }
        }
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = currentItem.completed
        holder.checkBox.setOnCheckedChangeListener {_,checked->
            holder.checkBox.isChecked = checked
            viewModel.setCompleteStatus(checked, currentItem)
        }
    }

    private fun getDueDate(dueDate: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date(dueDate))
    }


    fun selectAll(){
        taskList.forEach { it.selected = true }
        notifyDataSetChanged()
    }

    fun deselectAll(){
        taskList.forEach { it.selected = false }
        notifyDataSetChanged()
    }

    fun getSelected(): List<Task>{
        return taskList.filter { it.selected }
    }

    fun update(list: List<Task>){
        taskList.clear()
        taskList.addAll(list)
        notifyDataSetChanged()
    }

    fun swap(sp: Int,tp: Int){
        Collections.swap(taskList,sp,tp)
        notifyItemMoved(sp,tp)
    }

    fun showSelection(){

        isSelectMode = true
        notifyDataSetChanged()
    }
    fun hideSelection(){
        isSelectMode = false
        deselectAll()
        notifyDataSetChanged()
    }
    fun selectedCount(): Int{
        return taskList.count {it.selected}
    }

    override fun getItemCount(): Int = taskList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val checkBox = itemView.findViewById<CheckBox>(R.id.isCompleted)
        val taskText = itemView.findViewById<TextView>(R.id.rvTask)
        val dueDate = itemView.findViewById<TextView>(R.id.due_date)
        val select = itemView.findViewById<CheckBox>(R.id.isSelected)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val task = taskList[adapterPosition]
            itemOnclick(task)

        }
    }
}