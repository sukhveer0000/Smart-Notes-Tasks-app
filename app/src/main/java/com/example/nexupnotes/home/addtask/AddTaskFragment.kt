package com.example.nexupnotes.home.addtask

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.example.nexupnotes.R
import com.example.nexupnotes.databinding.FragmentAddTaskBinding
import com.example.nexupnotes.databinding.FragmentTasksBinding
import com.example.nexupnotes.model.Note
import com.example.nexupnotes.model.Task
import com.example.nexupnotes.utils.Constants
import com.example.nexupnotes.viewmodel.TasksViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTaskFragment : BottomSheetDialogFragment() {
    private  lateinit var viewModel: TasksViewModel
    private lateinit var binding: FragmentAddTaskBinding

    private var taskId = ""
    private var title =  ""
    private var dueDate: Long?  = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { iti ->
            taskId = iti.getString("taskId") ?: ""
            title = iti.getString("title") ?: ""
            dueDate = iti.getLong("dueDate",-1L).takeIf { it != -1L }
        }

        if (taskId.isNotEmpty()){
            binding.task.setText(title)
            binding.newTaskText.text = "Edit Task"
            binding.save.apply {
                isClickable = true
                setTextColor("#FF9800".toColorInt())
            }
        }


        viewModel = ViewModelProvider(requireActivity())[TasksViewModel::class.java]

        val textWatcher = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val task = binding.task.text.toString()
                val enable = task.isNotEmpty()
                if (enable){
                    binding.save.apply {
                        isClickable = true
                        setTextColor("#FF9800".toColorInt())
                    }
                }
                else {
                    binding.save.apply {
                        isClickable = false
                        setTextColor("#000000".toColorInt())
                    }
                }

            }

        }
        binding.task.addTextChangedListener(textWatcher)

        parentFragmentManager.setFragmentResultListener(
            "resultKey",
            viewLifecycleOwner
        ) { _, bundle ->
            dueDate = bundle.getLong("selectedDate",-1L).takeIf { it != -1L }
        }

        binding.reminderButton.setOnClickListener {
            val reminderBottomSheet = ReminderFragment()
            reminderBottomSheet.arguments = bundleOf("dueDate" to dueDate )
            reminderBottomSheet.show(parentFragmentManager,"ReminderFragment")
        }

        binding.save.setOnClickListener {
            val etTask = binding.task.text.toString().replaceFirstChar { it.uppercase() }

            if (taskId.isEmpty()) {
                viewModel.addTask(etTask, dueDate)
            }else {
                viewModel.updateTask(etTask,dueDate,taskId)
            }
            binding.task.setText("")
            dismiss()
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }


}