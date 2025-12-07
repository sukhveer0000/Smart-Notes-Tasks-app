package com.example.nexupnotes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nexupnotes.R
import com.example.nexupnotes.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesAdapter(
    val context: Context,
    val notesList: MutableList<Note>,
    val onItemClick: (Note) -> Unit
    ) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.note_card_view, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val currentItem = notesList[position]
        holder.rvTitle.text = currentItem.title.replaceFirstChar { it.uppercase() }
        val time = getTimeAgo(currentItem.timestamp)
        val content = currentItem.content
        holder.rvContent.text = "$time | $content"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(noteList: List<Note>) {
        notesList.clear()
        notesList.addAll(noteList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = notesList.size

    private fun getCurrentDateTime(timeStamp: Long): String {
        val sdf = SimpleDateFormat("dd MM yyyy, hh:MM a", Locale.getDefault())
        return sdf.format(Date(timeStamp))
    }

    private fun getTimeAgo(timeStamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - timeStamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hr ago"
            days == 1L -> "yesterday"
            else -> "$days ago"
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val rvTitle = itemView.findViewById<TextView>(R.id.rv_title)
        val rvContent = itemView.findViewById<TextView>(R.id.rv_time)

        init {
            itemView.setOnClickListener (this)
        }
        override fun onClick(v: View?) {
            val note = notesList[adapterPosition]
            onItemClick(note)
        }
    }


}