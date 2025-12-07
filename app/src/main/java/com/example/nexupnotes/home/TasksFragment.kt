package com.example.nexupnotes.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.nexupnotes.R
import com.example.nexupnotes.adapters.TaskAdapter
import com.example.nexupnotes.databinding.FragmentTasksBinding
import com.example.nexupnotes.home.addtask.AddTaskFragment
import com.example.nexupnotes.model.Task
import com.example.nexupnotes.viewmodel.TasksViewModel
import java.util.Collections


class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var viewModel: TasksViewModel
    private lateinit var adapter: TaskAdapter

    private var editMode = false

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater,container,false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.taskToolbar)

        viewModel = ViewModelProvider(this)[TasksViewModel::class.java]
        viewModel.getTask()

        adapter = TaskAdapter(viewModel,mutableListOf()){task ->
            val addTaskBottomSheet = AddTaskFragment()
            addTaskBottomSheet.arguments = bundleOf(
                "taskId" to task.taskId,
                "title" to task.title,
                "dueDate" to task.dueDate
            )
            if(!editMode){
                addTaskBottomSheet.show(parentFragmentManager,"AddTaskFragment")
            }

        }


        viewModel.taskList.observe(viewLifecycleOwner) { list ->
            adapter.update(list)
        }
        viewModel.selectionMode.observe(viewLifecycleOwner){isEnable->
            if (isEnable) adapter.showSelection()
            else adapter.hideSelection()
        }
        viewModel.selectedCount.observe(viewLifecycleOwner){ count->
            binding.tvBigTitle.text = "$count selected"

            binding.delete.isClickable = count != 0

            if (adapter.taskList.isNotEmpty() &&  count == adapter.taskList.size){
                binding.tvSelectAll.text = "Deselect All"
            }
            else{
                binding.tvSelectAll.text = "Select All"
            }
        }

        binding.rvTask.apply {
            layoutManager = GridLayoutManager(requireContext(),1)
            setHasFixedSize(true)
        }

        binding.rvTask.adapter = adapter

        val itemTouchHelper =ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0){
            override fun onMove(
                recyclerView: RecyclerView,
                source: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = source.adapterPosition
                val targetPosition = target.adapterPosition
                adapter.swap(sourcePosition,targetPosition)
                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                return
            }

        })
        itemTouchHelper.attachToRecyclerView(binding.rvTask)

        requireActivity().addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menuInflater.inflate(R.menu.task_toolbar_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.edit ->{
                        binding.delete.visibility = View.VISIBLE
                        binding.addTask.visibility = View.GONE
                        switchToolbar()
                        viewModel.enableSelectionMode()
                        editMode = true
                        return true
                    }
                    else -> false
                }
            }
        },viewLifecycleOwner)

        binding.tvCancel.setOnClickListener {
            resetUI()
        }

        binding.tvSelectAll.setOnClickListener {
            val text = binding.tvSelectAll.text.toString()
            if (text == "Select All"){
                binding.tvSelectAll.text = "Deselect All"
                adapter.selectAll()
                viewModel.selectAll(adapter.taskList)
            }else{
                binding.tvSelectAll.text = "Select All"
                adapter.deselectAll()
                viewModel.deselectAll(adapter.taskList)
            }
        }
//        delete
        binding.delete.setOnClickListener {

            val selectedTasks = adapter.getSelected()
            viewModel.deleteSelectedTasks(selectedTasks)
            adapter.taskList.removeAll(selectedTasks)

            binding.delete.visibility = View.GONE
            binding.addTask.visibility = View.VISIBLE

            switchToNormal()
            viewModel.disableSelectionMode()
            adapter.notifyDataSetChanged()
            viewModel.updateSelected(0)
        }

        binding.addTask.setOnClickListener {
            AddTaskFragment().show(parentFragmentManager,"AddTaskFragment")
        }

        return binding.root
    }
    private fun switchToolbar(){
        binding.taskToolbar.visibility = View.GONE
        binding.customView.visibility = View.VISIBLE

    }

    private fun switchToNormal() {
        binding.taskToolbar.visibility = View.VISIBLE
        binding.customView.visibility = View.GONE
    }

    fun resetUI(){
        binding.delete.visibility = View.GONE
        binding.addTask.visibility = View.VISIBLE
        switchToNormal()
        viewModel.deselectAll(adapter.taskList)
        viewModel.disableSelectionMode()
        editMode = false
        binding.tvSelectAll.text = "Select All"
    }

    override fun onPause() {
        super.onPause()
        resetUI()
    }
}


