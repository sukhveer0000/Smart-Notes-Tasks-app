package com.example.nexupnotes.home.addtask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.core.os.bundleOf
import com.example.nexupnotes.R
import com.example.nexupnotes.databinding.FragmentReminderBinding
import com.example.nexupnotes.databinding.FragmentTasksBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ReminderFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentReminderBinding
    private var selectDateMillis: Long? = null
    val months = arrayOf("January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December")

    private var dueDate: Long? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dueDate = arguments?.getLong("dueDate", -1L).takeIf { it != -1L }

        val initialDate = dueDate ?: System.currentTimeMillis()


        binding.calender.minDate = System.currentTimeMillis()
        binding.calender.date = initialDate

        binding.date.text = today(initialDate)

        binding.calender.setOnDateChangeListener { view, year, month, dayOfMonth ->

            val calender = java.util.Calendar.getInstance()
            calender.set(year,month,dayOfMonth)
            selectDateMillis = calender.timeInMillis

            val monthName = months[month]
            val selectDate = "$dayOfMonth $monthName $year"
            binding.date.text = selectDate
        }


        binding.save.setOnClickListener {
            val resultDate = selectDateMillis ?: dueDate
            parentFragmentManager.setFragmentResult("resultKey", bundleOf("selectedDate" to resultDate))
            dismiss()
        }

        binding.cancel.setOnClickListener {

            dismiss()
        }
    }

    private fun today(timestamp: Long) :String {

        val sdf = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }


}